package com.example.farmFeed.repository;

import java.util.List;
import java.util.Map;

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
        String sql = "SELECT fertilizer_id, name, price, stock, description FROM fertilizers";
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * Search fertilizers by name (case-insensitive) in a DB-safe way
     */
    public List<Map<String, Object>> searchByName(String name) {
        String sql = "SELECT fertilizer_id, name, price, stock, description FROM fertilizers WHERE LOWER(name) LIKE ?";
        String param = "%" + (name == null ? "" : name.toLowerCase()) + "%";
        return jdbcTemplate.queryForList(sql, param);
    }

    /**
     * Get fertilizer by ID
     */
    public Map<String, Object> getFertilizerById(Integer id) {
        String sql = "SELECT fertilizer_id, name, price, stock, description FROM fertilizers WHERE fertilizer_id = ?";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, id);
        return results.isEmpty() ? null : results.get(0);
    }
}