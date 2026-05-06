package com.example.farmFeed.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.farmFeed.entity.Farmer;
import com.example.farmFeed.repository.FarmerRepository;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class FarmerService {

    private static final Logger logger = LoggerFactory.getLogger(FarmerService.class);
    private static final Pattern BCRYPT_PATTERN = Pattern.compile("^\\$2[aby]\\$\\d{2}\\$.{53}$");

    @Autowired
    private FarmerRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Farmer save(Farmer farmer) {
        try {
            logger.info("saving farmer: {}", farmer.getPhone());
            // Encrypt password before saving
            if (!isBcryptHash(farmer.getPassword())) {
                farmer.setPassword(passwordEncoder.encode(farmer.getPassword()));
            }
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
        Optional<Farmer> farmer = repository.findByPhone(phone);
        if (farmer.isPresent() && passwordMatches(password, farmer.get().getPassword())) {
            return farmer;
        }
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public Optional<Farmer> loginByEmail(String email, String password) {
        logger.info("login attempt by email: {}", email);
        Optional<Farmer> farmer = repository.findByEmail(email);
        if (farmer.isPresent() && passwordMatches(password, farmer.get().getPassword())) {
            return farmer;
        }
        return Optional.empty();
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

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        return isBcryptHash(storedPassword) && passwordEncoder.matches(rawPassword, storedPassword);
    }

    private boolean isBcryptHash(String password) {
        return password != null && BCRYPT_PATTERN.matcher(password).matches();
    }
}
