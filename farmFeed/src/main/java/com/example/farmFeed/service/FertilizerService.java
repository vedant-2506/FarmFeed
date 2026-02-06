// package com.example.farmFeed.service;

// import java.util.List;
// import java.util.Map;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.example.farmFeed.repository.FertilizerRepository;

// @Service
// public class FertilizerService {

//     @Autowired
//     private FertilizerRepository repository;

//     public List<Map<String, Object>> fetchAll() {
//         return repository.getAllFertilizers();
//     }

//     public List<Map<String, Object>> searchFertilizer(String name) {
//         return repository.searchByName(name);
//     }
// }


package com.example.farmFeed.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.farmFeed.repository.FertilizerRepository;

@Service
public class FertilizerService {

    @Autowired
    private FertilizerRepository repository;

    /**
     * Get all fertilizers
     */
    public List<Map<String, Object>> fetchAll() {
        return repository.getAllFertilizers();
    }

    /**
     * Search fertilizers by name
     */
    public List<Map<String, Object>> searchFertilizer(String name) {
        if (name == null || name.trim().isEmpty()) {
            return repository.getAllFertilizers();
        }
        return repository.searchByName(name.trim());
    }

    /**
     * Get fertilizer by ID
     */
    public Map<String, Object> getFertilizerById(Integer id) {
        return repository.getFertilizerById(id);
    }
}