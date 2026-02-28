package com.example.farmFeed.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.farmFeed.entity.Vendor;
import com.example.farmFeed.repository.VendorRepository;
import java.util.Optional;

@Service
public class VendorService {

    private static final Logger logger = LoggerFactory.getLogger(VendorService.class);

    @Autowired
    private VendorRepository repository;

    /**
     * Register a new vendor (shopkeeper)
     */
    @Transactional
    public Vendor register(Vendor vendor) {
        try {
            logger.info("Registering vendor with email: {}", vendor.getEmail());
            Vendor savedVendor = repository.save(vendor);
            logger.info("Vendor registered successfully with ID: {}", savedVendor.getId());
            return savedVendor;
        } catch (Exception e) {
            logger.error("Error registering vendor: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Login vendor with email and password
     */
    @Transactional(readOnly = true)
    public Optional<Vendor> login(String email, String password) {
        logger.info("Attempting login for vendor email: {}", email);
        return repository.findByEmailAndPassword(email, password);
    }

    /**
     * Find vendor by email
     */
    @Transactional(readOnly = true)
    public Optional<Vendor> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    /**
     * Check if license number exists
     */
    @Transactional(readOnly = true)
    public Optional<Vendor> findByLicenseNumber(String licenseNumber) {
        return repository.findByLicenseNumber(licenseNumber);
    }

    /**
     * Find vendor by ID
     */
    @Transactional(readOnly = true)
    public Optional<Vendor> getVendorById(Long id) {
        return repository.findById(id);
    }
}

