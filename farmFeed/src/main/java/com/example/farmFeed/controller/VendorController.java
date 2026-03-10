package com.example.farmFeed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.farmFeed.entity.Vendor;
import com.example.farmFeed.service.VendorService;
import java.util.Optional;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/shopkeeper")
public class VendorController {

    private static final Logger logger = LoggerFactory.getLogger(VendorController.class);

    @Autowired
    private VendorService service;

    /**
     * POST /api/shopkeeper/register - Register a new vendor (shopkeeper)
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Vendor vendor) {
        try {
            logger.info("Registration request received for vendor email: {}", vendor.getEmail());

            // Check if email already exists
            Optional<Vendor> existingEmail = service.findByEmail(vendor.getEmail());
            if (existingEmail.isPresent()) {
                logger.warn("Email already registered: {}", vendor.getEmail());
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Email already registered"));
            }

            // Check if license number already exists
            Optional<Vendor> existingLicense = service.findByLicenseNumber(vendor.getLicenseNumber());
            if (existingLicense.isPresent()) {
                logger.warn("License number already registered: {}", vendor.getLicenseNumber());
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "License number already registered"));
            }

            logger.info("Saving new vendor: {} with email: {}", vendor.getShopName(), vendor.getEmail());
            Vendor savedVendor = service.register(vendor);

            logger.info("Vendor registered successfully! ID: {}, Email: {}", savedVendor.getId(), savedVendor.getEmail());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Registration successful",
                "shop_id", savedVendor.getId(),
                "email", savedVendor.getEmail(),
                "shop_name", savedVendor.getShopName(),
                "owner_name", savedVendor.getOwnerName()
            ));
        } catch (Exception e) {
            logger.error("Registration failed with error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", "Registration failed: " + e.getMessage()));
        }
    }

    /**
     * POST /api/shopkeeper/login - Login vendor with email and password
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            logger.info("Login attempt for vendor email: {}", email);

            if (email == null || password == null) {
                logger.warn("Login attempt without email or password");
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Email and password required"));
            }

            Optional<Vendor> vendor = service.login(email, password);

            if (vendor.isPresent()) {
                Vendor v = vendor.get();
                logger.info("Login successful for vendor email: {}", email);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Login successful",
                    "shop_id", v.getId(),
                    "email", v.getEmail(),
                    "shop_name", v.getShopName(),
                    "owner_name", v.getOwnerName(),
                    "licence_number", v.getLicenseNumber(),
                    "shop_address", v.getShopAddress()
                ));
            } else {
                logger.warn("Login failed - invalid credentials for vendor email: {}", email);
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Invalid email or password"));
            }
        } catch (Exception e) {
            logger.error("Login error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/shopkeeper/{id} - Get vendor details by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getVendor(@PathVariable Long id) {
        try {
            Optional<Vendor> vendor = service.getVendorById(id);
            if (vendor.isPresent()) {
                logger.info("Vendor fetched: {}", id);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "vendor", vendor.get()
                ));
            } else {
                logger.warn("Vendor not found: {}", id);
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "Vendor not found"));
            }
        } catch (Exception e) {
            logger.error("Error fetching vendor: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
