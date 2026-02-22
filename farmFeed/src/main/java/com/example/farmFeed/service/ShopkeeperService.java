package com.example.farmFeed.service;

import com.example.farmFeed.DTO.LoginRequest;
import com.example.farmFeed.DTO.ShopkeeperRegisterRequest;
import com.example.farmFeed.entity.Shopkeeper;
import com.example.farmFeed.repository.ShopkeeperRepository; // BUG FIX: was "shopkeeperRepository" (lowercase)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ============================================================
 * ShopkeeperService — Shopkeeper Authentication Service
 * ============================================================
 *
 * Handles all operations for /api/shopkeeper/** endpoints:
 *
 *   registerShopkeeper()      → POST /api/shopkeeper/register
 *   authenticateShopkeeper()  → POST /api/shopkeeper/login
 *   changePassword()          → PUT  /api/shopkeeper/change-password/{id}
 *   getShopkeeperById()       → GET  /api/shopkeeper/profile/{id}
 *
 * FLOW — Registration:
 *   1. Check if phone already registered (existsByPhone)
 *   2. Check if shop name already taken (existsByShopName)
 *   3. Validate password strength
 *   4. Hash password with BCrypt
 *   5. Save Shopkeeper to DB
 *
 * FLOW — Login:
 *   1. Find shopkeeper by phone
 *   2. Verify password with BCrypt
 *   3. Return Shopkeeper on success
 *
 * BUG FIXED:
 *   - Import was "shopkeeperRepository" (lowercase class name).
 *     On Linux (case-sensitive), this caused a compile error.
 *     Fixed to "ShopkeeperRepository" (PascalCase).
 */
@Service
public class ShopkeeperService {

    private static final Logger log = LoggerFactory.getLogger(ShopkeeperService.class);

    // BUG FIX: type updated from shopkeeperRepository → ShopkeeperRepository
    private final ShopkeeperRepository shopkeeperRepo;
    private final PasswordEncoder      passwordEncoder;

    public ShopkeeperService(ShopkeeperRepository shopkeeperRepo, PasswordEncoder passwordEncoder) {
        this.shopkeeperRepo = shopkeeperRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // ── Register ─────────────────────────────────────────────────────────────

    @Transactional
    public Shopkeeper registerShopkeeper(ShopkeeperRegisterRequest request) {
        log.info("Registering new shopkeeper with phone: {}", request.getPhone());

        if (shopkeeperRepo.existsByPhone(request.getPhone())) {
            log.warn("Phone already registered: {}", request.getPhone());
            throw new RuntimeException("Phone number already registered");
        }

        if (shopkeeperRepo.existsByShopName(request.getShopName())) {
            log.warn("Shop name already exists: {}", request.getShopName());
            throw new RuntimeException("Shop name already exists");
        }

        validatePasswordStrength(request.getPassword());

        Shopkeeper sk = new Shopkeeper();
        sk.setName(request.getName());
        sk.setShopName(request.getShopName());
        sk.setPhone(request.getPhone());
        sk.setAddress(request.getAddress());
        sk.setEmail(request.getEmail());
        sk.setLicenseNumber(request.getLicenseNumber());
        sk.setPassword(passwordEncoder.encode(request.getPassword()));

        Shopkeeper saved = shopkeeperRepo.save(sk);
        log.info("Shopkeeper registered with ID: {}", saved.getId());
        return saved;
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    public Shopkeeper authenticateShopkeeper(LoginRequest request) {
        log.info("Authenticating shopkeeper with phone: {}", request.getPhone());

        Shopkeeper sk = shopkeeperRepo.findByPhone(request.getPhone())
                .orElseThrow(() -> {
                    log.warn("Login failed - phone not found: {}", request.getPhone());
                    return new RuntimeException("Invalid phone number or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), sk.getPassword())) {
            log.warn("Login failed - wrong password for: {}", request.getPhone());
            throw new RuntimeException("Invalid phone number or password");
        }

        log.info("Shopkeeper authenticated: {}", request.getPhone());
        return sk;
    }

    // ── Change Password ───────────────────────────────────────────────────────

    @Transactional
    public void changePassword(Long shopkeeperId, String currentPassword, String newPassword) {
        log.info("Changing password for shopkeeper ID: {}", shopkeeperId);

        Shopkeeper sk = shopkeeperRepo.findById(shopkeeperId)
                .orElseThrow(() -> new RuntimeException("Shopkeeper not found"));

        if (!passwordEncoder.matches(currentPassword, sk.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        validatePasswordStrength(newPassword);
        sk.setPassword(passwordEncoder.encode(newPassword));
        shopkeeperRepo.save(sk);
        log.info("Password changed for shopkeeper ID: {}", shopkeeperId);
    }

    // ── Get Profile ───────────────────────────────────────────────────────────

    public Shopkeeper getShopkeeperById(Long shopkeeperId) {
        return shopkeeperRepo.findById(shopkeeperId)
                .orElseThrow(() -> new RuntimeException("Shopkeeper not found"));
    }

    // ── Helpers

    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");
        }
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);

        if (!hasUpper || !hasLower || !hasDigit) {
            throw new RuntimeException(
                "Password must contain at least one uppercase letter, one lowercase letter, and one digit"
            );
        }
    }
}