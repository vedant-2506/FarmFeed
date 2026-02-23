package com.example.farmFeed.service;

import com.example.farmFeed.entity.Farmer;
import com.example.farmFeed.repository.FarmerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * FarmerService — Farmer Service (Phone-Based, No Email).
 *
 * All farmer registration and login is done using phone number.
 * Email has been completely removed from the farmer flow.
 *
 * Methods:
 *   save()           → save a new farmer (hashes password)
 *   findByPhone()    → look up farmer by phone number
 *   loginByPhone()   → authenticate farmer with phone + password
 */
@Service
public class FarmerService {

    private static final Logger logger = LoggerFactory.getLogger(FarmerService.class);

    @Autowired
    private FarmerRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ── Save (Register) ───────────────────────────────────────────

    /**
     * Save a new farmer with a BCrypt-hashed password.
     * Called by: FarmerController POST /api/Farmer/SignUp
     */
    @Transactional
    public Farmer save(Farmer farmer) {
        logger.info("Saving new farmer with phone: {}", farmer.getPhone());

        if (repository.existsByPhone(farmer.getPhone())) {
            logger.warn("Phone already registered: {}", farmer.getPhone());
            throw new RuntimeException("Phone number already registered");
        }

        // Hash password before saving — never store plain text
        if (farmer.getPassword() != null && !farmer.getPassword().startsWith("$2a$")) {
            farmer.setPassword(passwordEncoder.encode(farmer.getPassword()));
        }

        Farmer saved = repository.save(farmer);
        logger.info("Farmer saved successfully with ID: {}", saved.getId());
        return saved;
    }

    // ── Find ──────────────────────────────────────────────────────

    /**
     * Find a farmer by phone number.
     * Called by: FarmerController duplicate check
     */
    public Optional<Farmer> findByPhone(String phone) {
        if (phone == null || phone.isBlank()) return Optional.empty();
        return repository.findByPhone(phone);
    }

    // ── Login ─────────────────────────────────────────────────────

    /**
     * Authenticate a farmer using phone + password.
     * Returns the Farmer if credentials are valid, empty Optional otherwise.
     * Called by: FarmerController POST /api/Farmer/Login
     */
    public Optional<Farmer> loginByPhone(String phone, String password) {
        if (phone == null || password == null) return Optional.empty();

        Optional<Farmer> farmerOpt = repository.findByPhone(phone);
        if (farmerOpt.isEmpty()) {
            logger.warn("Login failed — phone not found: {}", phone);
            return Optional.empty();
        }

        Farmer farmer = farmerOpt.get();
        if (!passwordEncoder.matches(password, farmer.getPassword())) {
            logger.warn("Login failed — wrong password for phone: {}", phone);
            return Optional.empty();
        }

        logger.info("Login successful for phone: {}", phone);
        return Optional.of(farmer);
    }
}