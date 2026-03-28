package com.example.farmFeed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.farmFeed.entity.Farmer;
import com.example.farmFeed.service.FarmerService;
import java.util.Optional;
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

            if (farmer.getFullName() == null || farmer.getFullName().isBlank() ||
                farmer.getAddress() == null || farmer.getAddress().isBlank() ||
                farmer.getPhone() == null || farmer.getPhone().isBlank() ||
                farmer.getPassword() == null || farmer.getPassword().isBlank()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Please fill all required farmer fields"));
            }
            
            // Check if phone already exists
            Optional<Farmer> existing = service.findByPhone(farmer.getPhone());
            if (existing.isPresent()) {
                logger.warn("Phone already registered: {}", farmer.getPhone());
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Phone number already registered"));
            }

            // Farmer signup does not ask for email in UI. Keep a generated value for DB compatibility.
            if (farmer.getEmail() == null || farmer.getEmail().isBlank()) {
                farmer.setEmail(farmer.getPhone() + "@farmfeed.local");
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

    /**
     * POST /api/farmer/reset-password - Reset farmer password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String phone = request.get("phone");
            String newPassword = request.get("password");

            if (phone == null || newPassword == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Phone and password required"));
            }

            Optional<Farmer> farmer = service.findByPhone(phone);
            if (farmer.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Farmer not found"));
            }

            Farmer f = farmer.get();
            f.setPassword(newPassword);
            service.save(f);

            logger.info("Password reset successful for phone: {}", phone);
            return ResponseEntity.ok(Map.of("success", true, "message", "Password reset successful"));
        } catch (Exception e) {
            logger.error("Password reset error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", "Password reset failed: " + e.getMessage()));
        }
    }

    /**
     * GET /api/farmer/{id} - Get farmer profile
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFarmerProfile(@PathVariable Long id) {
        try {
            Optional<Farmer> farmer = service.findById(id);
            if (farmer.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Farmer not found"));
            }

            Farmer f = farmer.get();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "farmer_id", f.getId(),
                "fullName", f.getFullName(),
                "phone", f.getPhone(),
                "email", f.getEmail(),
                "address", f.getAddress()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", "Failed to fetch profile"));
        }
    }
}