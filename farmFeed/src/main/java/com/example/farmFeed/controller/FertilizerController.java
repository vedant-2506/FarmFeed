package com.example.farmFeed.controller;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.farmFeed.service.FertilizerService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/fertilizers")
public class FertilizerController {

    @Autowired
    private FertilizerService service;

    /**
     * GET /api/fertilizers - Get all fertilizers
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllFertilizers() {
        List<Map<String, Object>> fertilizers = service.fetchAll();
        return ResponseEntity.ok(fertilizers);
    }

    /**
     * GET /api/fertilizers/search?name=urea - Search fertilizers
     */
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchFertilizer(
            @RequestParam(required = false) String name) {
        
        List<Map<String, Object>> fertilizers = service.searchFertilizer(name);
        return ResponseEntity.ok(fertilizers);
    }

    /**
     * GET /api/fertilizers/{id} - Get fertilizer by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getFertilizerById(@PathVariable Integer id) {
        Map<String, Object> fertilizer = service.getFertilizerById(id);
        if (fertilizer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(fertilizer);
    }
}