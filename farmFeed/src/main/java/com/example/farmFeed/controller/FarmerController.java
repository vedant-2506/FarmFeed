package com.example.farmFeed.controller;

import com.example.farmFeed.entity.Farmer;
import com.example.farmFeed.service.FarmerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * FarmerController — Farmer Registration & Login REST Controller.
 *
 * BASE URL: /api/Farmer
 *
 *   POST /api/Farmer/SignUp   → Register new farmer (name, phone, password, address)
 *   POST /api/Farmer/Login    → Login farmer (phone, password)
 *
 * Email is NOT used anywhere in this controller.
 * All auth is phone + password based.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/Farmer")
public class FarmerController {

    private static final Logger logger = LoggerFactory.getLogger(FarmerController.class);

    @Autowired
    private FarmerService service;

    // ── POST /api/Farmer/SignUp ────────────────────────────────────
    // Body: { "name":"Ram", "phone":"9876543210", "password":"Test1234!", "address":"Pune" }
    @PostMapping("/SignUp")
    public ResponseEntity<?> signUp(@RequestBody Farmer farmer) {
        try {
            logger.info("SignUp request for phone: {}", farmer.getPhone());

            // Validate required fields
            if (farmer.getName() == null || farmer.getName().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Name is required"));
            }
            if (farmer.getPhone() == null || farmer.getPhone().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Phone number is required"));
            }
            if (farmer.getPassword() == null || farmer.getPassword().length() < 8) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 8 characters"));
            }

            Farmer saved = service.save(farmer);

            logger.info("Farmer registered with ID: {}", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success",   true,
                "message",   "Sign up successful",
                "farmer_id", saved.getId(),
                "name",      saved.getName(),
                "phone",     saved.getPhone()
            ));

        } catch (RuntimeException e) {
            logger.error("SignUp failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── POST /api/Farmer/Login ─────────────────────────────────────
    // Body: { "phone":"9876543210", "password":"Test1234!" }
    @PostMapping("/Login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String phone    = credentials.get("phone");
            String password = credentials.get("password");

            logger.info("Login attempt for phone: {}", phone);

            if (phone == null || phone.isBlank() || password == null || password.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Phone and password are required"));
            }

            Optional<Farmer> farmerOpt = service.loginByPhone(phone, password);

            if (farmerOpt.isPresent()) {
                Farmer f = farmerOpt.get();
                logger.info("Login successful for phone: {}", phone);
                return ResponseEntity.ok(Map.of(
                    "success",   true,
                    "message",   "Login successful",
                    "farmer_id", f.getId(),
                    "name",      f.getName() != null ? f.getName() : "Farmer",
                    "phone",     f.getPhone(),
                    "userType",  "FARMER"
                ));
            } else {
                logger.warn("Login failed for phone: {}", phone);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid phone number or password"));
            }

        } catch (Exception e) {
            logger.error("Login error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}