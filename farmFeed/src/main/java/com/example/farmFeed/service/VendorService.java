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

    @Transactional
    public Vendor register(Vendor vendor) {
        try {
            logger.info("registering vendor: {}", vendor.getEmail());
            Vendor savedVendor = repository.save(vendor);
            logger.info("vendor registered with id: {}", savedVendor.getId());
            return savedVendor;
        } catch (Exception e) {
            logger.error("error registering vendor: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Optional<Vendor> login(String email, String password) {
        logger.info("vendor login: {}", email);
        return repository.findByEmailAndPassword(email, password);
    }

    @Transactional(readOnly = true)
    public Optional<Vendor> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<Vendor> findByLicenseNumber(String licenseNumber) {
        return repository.findByLicenseNumber(licenseNumber);
    }

    @Transactional(readOnly = true)
    public Optional<Vendor> getVendorById(Long id) {
        return repository.findById(id);
    }

    /**
     * Find vendor by ID (alias method)
     */
    @Transactional(readOnly = true)
    public Optional<Vendor> findById(Long id) {
        return repository.findById(id);
    }
}


