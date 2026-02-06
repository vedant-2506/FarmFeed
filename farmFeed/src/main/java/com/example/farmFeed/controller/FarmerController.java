package com.example.farmFeed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.farmFeed.entity.Farmer;
import com.example.farmFeed.service.FarmerService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/Farmer")
public class FarmerController {

    @Autowired
    private FarmerService service;

    @PostMapping("/SignUp")
    public ResponseEntity<Farmer> SignUp(@RequestBody Farmer farmer) {
        Farmer savedFarmer = service.save(farmer);
        return ResponseEntity.ok(savedFarmer);
    }
}

