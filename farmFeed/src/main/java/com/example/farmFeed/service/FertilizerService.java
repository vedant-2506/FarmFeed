package com.example.farmFeed.service;

import com.example.farmFeed.repository.FertilizerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * ============================================================
 * FertilizerService — Business Logic for Fertilizer Data
 * ============================================================
 *
 * Delegates all DB operations to FertilizerRepository (JDBC).
 * Results come back as List<Map<String, Object>> — each map
 * is one table row: { "fertilizer_id" → 1, "name" → "Urea", … }
 *
 * Used by: FertilizerController → /api/fertilizers/**
 */
@Service
public class FertilizerService {

    @Autowired
    private FertilizerRepository repository;

    /** Return all fertilizers from the database. */
    public List<Map<String, Object>> fetchAll() {
        return repository.getAllFertilizers();
    }

    /**
     * Search by name (case-insensitive partial match).
     * If name is blank, return everything.
     */
    public List<Map<String, Object>> searchFertilizer(String name) {
        if (name == null || name.trim().isEmpty()) {
            return repository.getAllFertilizers();
        }
        return repository.searchByName(name.trim());
    }

    /** Return a single fertilizer by ID, or null if not found. */
    public Map<String, Object> getFertilizerById(Integer id) {
        return repository.getFertilizerById(id);
    }
}