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

    @Transactional
    public Farmer save(Farmer farmer) {
        try {
            logger.info("saving farmer: {}", farmer.getPhone());
            Farmer savedFarmer = repository.save(farmer);
            logger.info("farmer saved with id: {}", savedFarmer.getId());
            return savedFarmer;
        } catch (Exception e) {
            logger.error("error saving farmer: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Optional<Farmer> login(String phone, String password) {
        logger.info("login attempt: {}", phone);
        return repository.findByPhoneAndPassword(phone, password);
    }

    @Transactional(readOnly = true)
    public Optional<Farmer> findByPhone(String phone) {
        return repository.findByPhone(phone);
    }

    @Transactional(readOnly = true)
    public Optional<Farmer> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    /**
     * Find farmer by ID
     */
    @Transactional(readOnly = true)
    public Optional<Farmer> findById(Long id) {
        return repository.findById(id);
    }
}
