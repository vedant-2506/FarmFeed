package com.example.farmFeed.service;

import com.example.farmFeed.dto.LoginRequest;
import com.example.farmFeed.dto.RegisterRequest;
import com.example.farmFeed.entity.Farmer;
import com.example.farmFeed.repository.FarmerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final FarmerRepository farmerRepository;
    private final PasswordEncoder  passwordEncoder;

    public AuthService(FarmerRepository farmerRepository, PasswordEncoder passwordEncoder) {
        this.farmerRepository = farmerRepository;
        this.passwordEncoder  = passwordEncoder;
    }

    // ── Register ──────────────────────────────────────────────────
    @Transactional
    public Farmer registerFarmer(RegisterRequest request) {
        log.info("Registering farmer with phone: {}", request.getPhone());

        if (farmerRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already registered");
        }

        Farmer farmer = new Farmer();
        farmer.setName(request.getName());
        farmer.setPhone(request.getPhone());
        farmer.setAddress(request.getAddress());
        farmer.setPassword(passwordEncoder.encode(request.getPassword()));

        Farmer saved = farmerRepository.save(farmer);
        log.info("Farmer registered with ID: {}", saved.getId());
        return saved;
    }

    // ── Login ─────────────────────────────────────────────────────
    public Farmer authenticateFarmer(LoginRequest request) {
        log.info("Authenticating farmer with phone: {}", request.getPhone());

        Farmer farmer = farmerRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new RuntimeException("Invalid phone number or password"));

        if (!passwordEncoder.matches(request.getPassword(), farmer.getPassword())) {
            log.warn("Wrong password for phone: {}", request.getPhone());
            throw new RuntimeException("Invalid phone number or password");
        }

        log.info("Farmer authenticated: {}", request.getPhone());
        return farmer;
    }

    //Change Password 
    @Transactional
    public void changePassword(Long farmerId, String currentPassword, String newPassword) {
        log.info("Changing password for farmer ID: {}", farmerId);

        if (farmerId == null) {
            throw new RuntimeException("Farmer ID cannot be null");
        }
        
        Farmer farmer = farmerRepository.findById(farmerId)
            .orElseThrow(() -> new RuntimeException("Farmer not found"));

        if (!passwordEncoder.matches(currentPassword, farmer.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException("New password must be at least 8 characters");
        }

        farmer.setPassword(passwordEncoder.encode(newPassword));
        farmerRepository.save(farmer);
        log.info("Password updated for farmer ID: {}", farmerId);
    }

    // ── Get Profile ───────────────────────────────────────────────
    public Farmer getFarmerById(Long farmerId) {
        if (farmerId == null) {
            throw new RuntimeException("Farmer ID cannot be null");
        }
        return farmerRepository.findById(farmerId)
            .orElseThrow(() -> new RuntimeException("Farmer not found"));
    }
}

// package com.example.farmFeed.service;

// import com.example.farmFeed.DTO.LoginRequest;
// import com.example.farmFeed.DTO.RegisterRequest;
// import com.example.farmFeed.entity.Farmer;
// import com.example.farmFeed.repository.FarmerRepository;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// @Service
// public class AuthService {

//     private static final Logger log = LoggerFactory.getLogger(AuthService.class);

//     private final FarmerRepository farmerRepository;
//     private final PasswordEncoder passwordEncoder;

//     public AuthService(FarmerRepository farmerRepository,
//                        PasswordEncoder passwordEncoder) {
//         this.farmerRepository = farmerRepository;
//         this.passwordEncoder = passwordEncoder;
//     }

//     //REGISTER FARMER
//     @Transactional
//     public Farmer registerFarmer(RegisterRequest request) {

//         log.info("Register request received for phone: {}", request.getPhone());

//         // Check existing phone
//         if (farmerRepository.existsByPhone(request.getPhone())) {
//             log.warn("Phone already registered: {}", request.getPhone());
//             throw new RuntimeException("Phone number already registered");
//         }

//         // Password validation
//         validatePassword(request.getPassword());

//         Farmer farmer = new Farmer();
//         farmer.setName(request.getName());
//         farmer.setPhone(request.getPhone());
//         farmer.setAddress(request.getAddress());

//         // Encrypt password
//         farmer.setPassword(passwordEncoder.encode(request.getPassword()));

//         Farmer savedFarmer = farmerRepository.save(farmer);
//         log.info("Farmer registered successfully with ID: {}", savedFarmer.getId());

//         return savedFarmer;
//     }

//     //LOGIN FARMER
//     public Farmer authenticateFarmer(LoginRequest request) {

//         log.info("Login attempt for phone: {}", request.getPhone());

//         Farmer farmer = farmerRepository.findByPhone(request.getPhone())
//                 .orElseThrow(() -> {
//                     log.warn("Invalid phone: {}", request.getPhone());
//                     return new RuntimeException("Invalid phone or password");
//                 });

//         if (!passwordEncoder.matches(request.getPassword(), farmer.getPassword())) {
//             log.warn("Wrong password for phone: {}", request.getPhone());
//             throw new RuntimeException("Invalid phone or password");
//         }

//         log.info("Login successful for phone: {}", request.getPhone());
//         return farmer;
//     }

//     //CHANGE PASSWORD
//     @Transactional
//     public void changePassword(Long farmerId,
//                                String currentPassword,
//                                String newPassword) {

//         log.info("Password change request for farmer ID: {}", farmerId);

//         if (farmerId == null) {
//             throw new RuntimeException("Farmer ID cannot be null");
//         }

//         Farmer farmer = farmerRepository.findById(farmerId)
//             .orElseThrow(() -> new RuntimeException("Farmer not found"));

//         // Validate current password
//         if (!passwordEncoder.matches(currentPassword, farmer.getPassword())) {
//             log.warn("Wrong current password for farmer ID: {}", farmerId);
//             throw new RuntimeException("Current password is incorrect");
//         }

//         // Validate new password
//         validatePassword(newPassword);

//         farmer.setPassword(passwordEncoder.encode(newPassword));
//         farmerRepository.save(farmer);

//         log.info("Password changed successfully for farmer ID: {}", farmerId);
//     }

//     //GET FARMER PROFILE
//     public Farmer getFarmerById(Long farmerId) {

//         log.info("Fetching farmer profile ID: {}", farmerId);

//         if (farmerId == null) {
//             throw new RuntimeException("Farmer ID cannot be null");
//         }
//         return farmerRepository.findById(farmerId)
//             .orElseThrow(() -> new RuntimeException("Farmer not found"));
//     }

//     //COMMON PASSWORD VALIDATION
//     private void validatePassword(String password) {

//         if (password == null || password.length() < 8) {
//             throw new RuntimeException("Password must be at least 8 characters");
//         }

//         if (!password.matches(".*[A-Z].*")) {
//             throw new RuntimeException("Password must contain at least one uppercase letter");
//         }

//         if (!password.matches(".*[a-z].*")) {
//             throw new RuntimeException("Password must contain at least one lowercase letter");
//         }

//         if (!password.matches(".*\\d.*")) {
//             throw new RuntimeException("Password must contain at least one number");
//         }
//     }
// }