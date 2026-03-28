package com.example.farmFeed.repository;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FertilizerRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Fetch all fertilizers from database
     */
    public List<Map<String, Object>> getAllFertilizers() {
        if (tableExists("bighaat_products_raw")) {
            String sql = """
                SELECT
                    mysql_import_key AS fertilizer_id,
                    product_name AS name,
                    COALESCE(CAST(price_inr AS SIGNED), 0) AS price,
                    100 AS stock,
                    COALESCE(NULLIF(description_clean, ''), detailed_description_10_sentences) AS description,
                    image_link AS image_url,
                    primary_category,
                    subcategory,
                    CASE
                        WHEN LOWER(CONCAT(IFNULL(product_name, ''), ' ', IFNULL(description_clean, ''))) LIKE '%organic%' THEN 'Organic'
                        WHEN LOWER(CONCAT(IFNULL(product_name, ''), ' ', IFNULL(description_clean, ''))) LIKE '%chemical%'
                             OR LOWER(CONCAT(IFNULL(product_name, ''), ' ', IFNULL(description_clean, ''))) LIKE '%urea%'
                             OR LOWER(CONCAT(IFNULL(product_name, ''), ' ', IFNULL(description_clean, ''))) LIKE '%dap%'
                             OR LOWER(CONCAT(IFNULL(product_name, ''), ' ', IFNULL(description_clean, ''))) LIKE '%potash%'
                             OR LOWER(CONCAT(IFNULL(product_name, ''), ' ', IFNULL(description_clean, ''))) LIKE '%npk%'
                        THEN 'Chemical'
                        ELSE 'Other'
                    END AS category
                FROM bighaat_products_raw
                WHERE product_name IS NOT NULL
            """;
            return jdbcTemplate.queryForList(sql);
        }

        if (!tableExists("fertilizers")) {
            return List.of();
        }

        String sql = """
            SELECT
                fertilizer_id,
                name,
                price,
                stock,
                description,
                NULL AS image_url,
                CASE
                    WHEN LOWER(CONCAT(IFNULL(name, ''), ' ', IFNULL(description, ''))) LIKE '%organic%' THEN 'Organic'
                    WHEN LOWER(CONCAT(IFNULL(name, ''), ' ', IFNULL(description, ''))) LIKE '%chemical%'
                         OR LOWER(CONCAT(IFNULL(name, ''), ' ', IFNULL(description, ''))) LIKE '%urea%'
                         OR LOWER(CONCAT(IFNULL(name, ''), ' ', IFNULL(description, ''))) LIKE '%dap%'
                         OR LOWER(CONCAT(IFNULL(name, ''), ' ', IFNULL(description, ''))) LIKE '%potash%'
                         OR LOWER(CONCAT(IFNULL(name, ''), ' ', IFNULL(description, ''))) LIKE '%npk%'
                    THEN 'Chemical'
                    ELSE 'Other'
                END AS category
            FROM fertilizers
        """;
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * Search fertilizers by name (case-insensitive) in a DB-safe way
     */
    public List<Map<String, Object>> searchByName(String name) {
        if (!tableExists("fertilizers")) {
            return List.of();
        }
        String sql = "SELECT fertilizer_id, name, price, stock, description, NULL AS image_url, 'Other' AS category FROM fertilizers WHERE LOWER(name) LIKE ?";
        String param = "%" + (name == null ? "" : name.toLowerCase()) + "%";
        return jdbcTemplate.queryForList(sql, param);
    }

    /**
     * Get fertilizer by ID
     */
    public Map<String, Object> getFertilizerById(Integer id) {
        if (!tableExists("fertilizers")) {
            return null;
        }
        String sql = "SELECT fertilizer_id, name, price, stock, description, NULL AS image_url, 'Other' AS category FROM fertilizers WHERE fertilizer_id = ?";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, id);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Get multiple fertilizers by IDs in a single query (optimized to avoid N+1 problem)
     * Handles both numeric IDs (from fertilizers table) and string IDs (bighaat_1 format)
     */
    public List<Map<String, Object>> getFertilizersByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        
        if (tableExists("bighaat_products_raw")) {
            //Build bighaat IDs like "bighaat_1", "bighaat_10", etc. for the IN clause
            StringBuilder inClause = new StringBuilder();
            List<String> bighaat = new ArrayList<>();
            for (Integer id : ids) {
                bighaat.add("bighaat_" + id);
            }
            String placeholders = String.join(",", bighaat.stream().map(id -> "?").toList());
            String sql = "SELECT mysql_import_key AS fertilizer_id, product_name AS name, COALESCE(CAST(price_inr AS SIGNED), 0) AS price, 100 AS stock, COALESCE(NULLIF(description_clean, ''), detailed_description_10_sentences) AS description, image_link AS image_url, 'Other' AS category FROM bighaat_products_raw WHERE mysql_import_key IN (" + placeholders + ")";
            return jdbcTemplate.queryForList(sql, bighaat.toArray());
        }

        if (!tableExists("fertilizers")) {
            return List.of();
        }

        String placeholders = String.join(",", ids.stream().map(id -> "?").toList());
        String sql = "SELECT fertilizer_id, name, price, stock, description, NULL AS image_url, 'Other' AS category FROM fertilizers WHERE fertilizer_id IN (" + placeholders + ")";
        return jdbcTemplate.queryForList(sql, ids.toArray());
    }

    private boolean tableExists(String tableName) {
        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
        return count != null && count > 0;
    }
}