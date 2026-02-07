package com.example.farmFeed.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.farmFeed.entity.Farmer;
import com.example.farmFeed.repository.FarmerRepository;
import java.util.Optional;

@Service
public class FarmerService {

    private static final Logger logger = LoggerFactory.getLogger(FarmerService.class);

    @Autowired
    private FarmerRepository repository;

    /**
     * Sign up a new farmer
     */
    @Transactional
    public Farmer save(Farmer farmer) {
        try {
            logger.info("Saving farmer with email: {}", farmer.getEmail());
            Farmer savedFarmer = repository.save(farmer);
            logger.info("Farmer saved successfully with ID: {}", savedFarmer.getId());
            return savedFarmer;
        } catch (Exception e) {
            logger.error("Error saving farmer: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Login farmer with email and password
     */
    @Transactional(readOnly = true)
    public Optional<Farmer> login(String email, String password) {
        logger.info("Attempting login for email: {}", email);
        return repository.findByEmailAndPassword(email, password);
    }

    /**
     * Find farmer by email
     */
    @Transactional(readOnly = true)
    public Optional<Farmer> findByEmail(String email) {
        return repository.findByEmail(email);
    }
}
