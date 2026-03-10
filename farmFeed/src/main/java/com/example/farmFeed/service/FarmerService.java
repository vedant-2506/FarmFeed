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
            logger.info("Saving farmer with phone: {}", farmer.getPhone());
            Farmer savedFarmer = repository.save(farmer);
            logger.info("Farmer saved successfully with ID: {}", savedFarmer.getId());
            return savedFarmer;
        } catch (Exception e) {
            logger.error("Error saving farmer: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Login farmer with phone and password
     */
    @Transactional(readOnly = true)
    public Optional<Farmer> login(String phone, String password) {
        logger.info("Attempting login for phone: {}", phone);
        return repository.findByPhoneAndPassword(phone, password);
    }

    /**
     * Find farmer by phone
     */
    @Transactional(readOnly = true)
    public Optional<Farmer> findByPhone(String phone) {
        return repository.findByPhone(phone);
    }
}
