package com.example.farmFeed.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * ============================================================
 * FertilizerRepository — JDBC Repository for Fertilizer data
 * ============================================================
 *
 * Uses JdbcTemplate (raw SQL) instead of JPA because:
 *   - Fertilizer queries may return image BLOBs
 *   - Raw SQL gives full control over which columns to fetch
 *   - No need for a Fertilizer @Entity class
 *
 * Results are returned as List<Map<String, Object>> where each
 * Map represents one row: { "fertilizer_id" → 1, "name" → "Urea", … }
 *
 * Used by: FertilizerService → FertilizerController
 */
@Repository
public class FertilizerRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Fetch all fertilizers (id, name, price, stock, description).
     * Image blob is excluded from list view for performance.
     */
    public List<Map<String, Object>> getAllFertilizers() {
        String sql = "SELECT fertilizer_id, name, price, stock, description FROM fertilizers";
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * Search fertilizers by name (case-insensitive partial match).
     * Uses LIKE with LOWER() for portability across H2 and MySQL.
     */
    public List<Map<String, Object>> searchByName(String name) {
        String sql = "SELECT fertilizer_id, name, price, stock, description "
                   + "FROM fertilizers WHERE LOWER(name) LIKE ?";
        String param = "%" + (name == null ? "" : name.toLowerCase()) + "%";
        return jdbcTemplate.queryForList(sql, param);
    }

    /**
     * Get a single fertilizer by its ID.
     * Returns null if not found (handled in FertilizerController).
     */
    public Map<String, Object> getFertilizerById(Integer id) {
        String sql = "SELECT fertilizer_id, name, price, stock, description "
                   + "FROM fertilizers WHERE fertilizer_id = ?";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, id);
        return results.isEmpty() ? null : results.get(0);
    }
}