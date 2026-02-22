// package com.example.farmFeed.service;

// import com.example.farmFeed.entity.Farmer;
// import com.example.farmFeed.repository.FarmerRepository;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.Optional;

// /**
//  * FarmerService — Farmer Service (Phone-Based, No Email).
//  *
//  * All farmer registration and login is done using phone number.
//  * Email has been completely removed from the farmer flow.
//  *
//  * Methods:
//  *   save()           → save a new farmer (hashes password)
//  *   findByPhone()    → look up farmer by phone number
//  *   loginByPhone()   → authenticate farmer with phone + password
//  */
// @Service
// public class FarmerService {

//     private static final Logger logger = LoggerFactory.getLogger(FarmerService.class);

//     @Autowired
//     private FarmerRepository repository;

//     @Autowired
//     private PasswordEncoder passwordEncoder;

//     // ── Save (Register) ───────────────────────────────────────────

//     /**
//      * Save a new farmer with a BCrypt-hashed password.
//      * Called by: FarmerController POST /api/Farmer/SignUp
//      */
//     @Transactional
//     public Farmer save(Farmer farmer) {
//         logger.info("Saving new farmer with phone: {}", farmer.getPhone());

//         if (repository.existsByPhone(farmer.getPhone())) {
//             logger.warn("Phone already registered: {}", farmer.getPhone());
//             throw new RuntimeException("Phone number already registered");
//         }

//         // Hash password before saving — never store plain text
//         if (farmer.getPassword() != null && !farmer.getPassword().startsWith("$2a$")) {
//             farmer.setPassword(passwordEncoder.encode(farmer.getPassword()));
//         }

//         Farmer saved = repository.save(farmer);
//         logger.info("Farmer saved successfully with ID: {}", saved.getId());
//         return saved;
//     }

//     // ── Find ──────────────────────────────────────────────────────

//     /**
//      * Find a farmer by phone number.
//      * Called by: FarmerController duplicate check
//      */
//     public Optional<Farmer> findByPhone(String phone) {
//         if (phone == null || phone.isBlank()) return Optional.empty();
//         return repository.findByPhone(phone);
//     }

//     // ── Login ─────────────────────────────────────────────────────

//     /**
//      * Authenticate a farmer using phone + password.
//      * Returns the Farmer if credentials are valid, empty Optional otherwise.
//      * Called by: FarmerController POST /api/Farmer/Login
//      */
//     public Optional<Farmer> loginByPhone(String phone, String password) {
//         if (phone == null || password == null) return Optional.empty();

//         Optional<Farmer> farmerOpt = repository.findByPhone(phone);
//         if (farmerOpt.isEmpty()) {
//             logger.warn("Login failed — phone not found: {}", phone);
//             return Optional.empty();
//         }

//         Farmer farmer = farmerOpt.get();
//         if (!passwordEncoder.matches(password, farmer.getPassword())) {
//             logger.warn("Login failed — wrong password for phone: {}", phone);
//             return Optional.empty();
//         }

//         logger.info("Login successful for phone: {}", phone);
//         return Optional.of(farmer);
//     }
// }



package com.example.farmFeed.service;

import com.example.farmFeed.DTO.LoginRequest;
import com.example.farmFeed.DTO.RegisterRequest;
import com.example.farmFeed.entity.Farmer;
import com.example.farmFeed.repository.FarmerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthService — Farmer authentication service.
 *
 * ROOT CAUSE OF IMPORT ERROR:
 *   The file AuthService.java contained "public class FarmerService" inside it.
 *   Java requires the public class name to EXACTLY match the filename.
 *   Because the filename is AuthService.java but the class was FarmerService,
 *   the compiler could never produce a valid AuthService class — so the import
 *   "import com.example.farmFeed.service.AuthService" resolved to nothing.
 *
 * Used by: AuthController → /api/auth/**
 *
 * Methods:
 *   registerFarmer()      → POST /api/auth/register
 *   authenticateFarmer()  → POST /api/auth/login
 *   changePassword()      → PUT  /api/auth/change-password/{id}
 *   getFarmerById()       → GET  /api/auth/profile/{id}
 */
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

    // ── Change Password ───────────────────────────────────────────
    @Transactional
    public void changePassword(Long farmerId, String currentPassword, String newPassword) {
        log.info("Changing password for farmer ID: {}", farmerId);

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
        return farmerRepository.findById(farmerId)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
    }
}


