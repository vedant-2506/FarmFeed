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
@RequestMapping("/api/Farmer")
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
            logger.info("Sign up request received for email: {}", farmer.getEmail());
            
            // Check if email already exists
            Optional<Farmer> existing = service.findByEmail(farmer.getEmail());
            if (existing.isPresent()) {
                logger.warn("Email already registered: {}", farmer.getEmail());
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email already registered"));
            }

            logger.info("Saving new farmer: {} with email: {}", farmer.getName(), farmer.getEmail());
            Farmer savedFarmer = service.save(farmer);
            
            logger.info("Farmer saved successfully! ID: {}, Email: {}", savedFarmer.getId(), savedFarmer.getEmail());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Sign up successful",
                "farmer_id", savedFarmer.getId(),
                "email", savedFarmer.getEmail()
            ));
        } catch (Exception e) {
            logger.error("Sign up failed with error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Sign up failed: " + e.getMessage()));
        }
    }

    /**
     * POST /api/Farmer/Login - Login farmer
     */
    @PostMapping("/Login")
    public ResponseEntity<?> Login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            logger.info("Login attempt for email: {}", email);

            if (email == null || password == null) {
                logger.warn("Login attempt without email or password");
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email and password required"));
            }

            Optional<Farmer> farmer = service.login(email, password);

            if (farmer.isPresent()) {
                Farmer f = farmer.get();
                logger.info("Login successful for: {}", email);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Login successful",
                    "farmer_id", f.getId(),
                    "email", f.getEmail(),
                    "name", f.getName() != null ? f.getName() : "Farmer"
                ));
            } else {
                logger.warn("Login failed - invalid credentials for email: {}", email);
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid email or password"));
            }
        } catch (Exception e) {
            logger.error("Login error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}

