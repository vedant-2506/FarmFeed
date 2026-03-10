package com.example.farmFeed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.farmFeed.entity.Farmer;
import com.example.farmFeed.service.FarmerService;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/farmer")
public class FarmerController {

    private static final Logger logger = LoggerFactory.getLogger(FarmerController.class);

    @Autowired
    private FarmerService service;

    /**
     * POST /api/Farmer/SignUp - Register a new farmer
     */
    @PostMapping("/SignUp")
    public ResponseEntity<?> SignUp(@RequestBody Farmer farmer) {
        try {
            logger.info("Sign up request received for phone: {}", farmer.getPhone());
            
            // Check if phone already exists
            Optional<Farmer> existing = service.findByPhone(farmer.getPhone());
            if (existing.isPresent()) {
                logger.warn("Phone already registered: {}", farmer.getPhone());
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Phone number already registered"));
            }

            logger.info("Saving new farmer: {} with phone: {}", farmer.getFullName(), farmer.getPhone());
            Farmer savedFarmer = service.save(farmer);
            
            logger.info("Farmer saved successfully! ID: {}, Phone: {}", savedFarmer.getId(), savedFarmer.getPhone());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Sign up successful",
                "farmer_id", savedFarmer.getId(),
                "phone", savedFarmer.getPhone(),
                "fullName", savedFarmer.getFullName()
            ));
        } catch (Exception e) {
            logger.error("Sign up failed with error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", "Sign up failed: " + e.getMessage()));
        }
    }

    /**
     * POST /api/Farmer/Login - Login farmer with phone and password
     */
    @PostMapping("/Login")
    public ResponseEntity<?> Login(@RequestBody Map<String, String> credentials) {
        try {
            String phone = credentials.get("phone");
            String password = credentials.get("password");

            logger.info("Login attempt for phone: {}", phone);

            if (phone == null || password == null) {
                logger.warn("Login attempt without phone or password");
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Phone number and password required"));
            }

            Optional<Farmer> farmer = service.login(phone, password);

            if (farmer.isPresent()) {
                Farmer f = farmer.get();
                logger.info("Login successful for phone: {}", phone);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Login successful",
                    "farmer_id", f.getId(),
                    "phone", f.getPhone(),
                    "fullName", f.getFullName() != null ? f.getFullName() : "Farmer",
                    "address", f.getAddress()
                ));
            } else {
                logger.warn("Login failed - invalid credentials for phone: {}", phone);
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Invalid phone number or password"));
            }
        } catch (Exception e) {
            logger.error("Login error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}