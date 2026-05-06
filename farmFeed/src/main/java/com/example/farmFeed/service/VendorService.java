package com.example.farmFeed.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.farmFeed.entity.Vendor;
import com.example.farmFeed.repository.VendorRepository;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class VendorService {

    private static final Logger logger = LoggerFactory.getLogger(VendorService.class);
    private static final Pattern BCRYPT_PATTERN = Pattern.compile("^\\$2[aby]\\$\\d{2}\\$.{53}$");

    @Autowired
    private VendorRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Vendor register(Vendor vendor) {
        try {
            logger.info("registering vendor: {}", vendor.getEmail());
            // Encrypt password before saving
            if (!isBcryptHash(vendor.getPassword())) {
                vendor.setPassword(passwordEncoder.encode(vendor.getPassword()));
            }
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
        Optional<Vendor> vendor = repository.findByEmail(email);
        if (vendor.isPresent() && passwordMatches(password, vendor.get().getPassword())) {
            return vendor;
        }
        return Optional.empty();
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

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        return isBcryptHash(storedPassword) && passwordEncoder.matches(rawPassword, storedPassword);
    }

    private boolean isBcryptHash(String password) {
        return password != null && BCRYPT_PATTERN.matcher(password).matches();
    }
}
