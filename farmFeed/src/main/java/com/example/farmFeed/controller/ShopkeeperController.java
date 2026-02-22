package com.example.farmFeed.controller;

import com.example.farmFeed.dto.ChangePasswordRequest;
import com.example.farmFeed.dto.LoginRequest;
import com.example.farmFeed.dto.ShopkeeperRegisterRequest;
import com.example.farmFeed.entity.Shopkeeper;
import com.example.farmFeed.service.ShopkeeperService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ============================================================
 * ShopkeeperController — Shopkeeper Authentication REST Controller
 * ============================================================
 *
 * BASE URL: /api/shopkeeper
 *
 * ┌──────────────────────────────────────────────┬──────────────────────────────┐
 * │ Endpoint                                     │ Description                  │
 * ├──────────────────────────────────────────────┼──────────────────────────────┤
 * │ POST /api/shopkeeper/register                │ Register new shopkeeper      │
 * │ POST /api/shopkeeper/login                   │ Shopkeeper login             │
 * │ PUT  /api/shopkeeper/change-password/{id}    │ Change password              │
 * │ GET  /api/shopkeeper/profile/{id}            │ Get shopkeeper profile       │
 * └──────────────────────────────────────────────┴──────────────────────────────┘
 */
@RestController
@RequestMapping("/api/shopkeeper")
@CrossOrigin(origins = "*")
public class ShopkeeperController {

    private static final Logger log = LoggerFactory.getLogger(ShopkeeperController.class);

    private final ShopkeeperService shopkeeperService;

    public ShopkeeperController(ShopkeeperService shopkeeperService) {
        this.shopkeeperService = shopkeeperService;
    }

    // ── POST /api/shopkeeper/register ─────────────────────────────────────────
    // Body: { "name":"Suresh", "shopName":"Green Store", "phone":"9876543210",
    //         "password":"Test1234!", "address":"Pune", "email":"...", "licenseNumber":"..." }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody ShopkeeperRegisterRequest request) {
        try {
            log.info("Shopkeeper registration for phone: {}", request.getPhone());
            Shopkeeper sk = shopkeeperService.registerShopkeeper(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SuccessResponse("Registration successful",
                            sk.getId(), sk.getName(), sk.getPhone(), sk.getShopName()));
        } catch (RuntimeException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // ── POST /api/shopkeeper/login ────────────────────────────────────────────
    // Body: { "phone":"9876543210", "password":"Test1234!" }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            log.info("Shopkeeper login for phone: {}", request.getPhone());
            Shopkeeper sk = shopkeeperService.authenticateShopkeeper(request);
            return ResponseEntity.ok(new LoginResponse("Login successful",
                    sk.getId(), sk.getName(), sk.getPhone(),
                    sk.getShopName(), sk.getAddress(), "SHOPKEEPER"));
        } catch (RuntimeException e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // ── PUT /api/shopkeeper/change-password/{shopkeeperId} ────────────────────
    @PutMapping("/change-password/{shopkeeperId}")
    public ResponseEntity<?> changePassword(
            @PathVariable Long shopkeeperId,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            log.info("Password change for shopkeeper ID: {}", shopkeeperId);
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("New password and confirm password do not match"));
            }
            shopkeeperService.changePassword(shopkeeperId,
                    request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok(new SuccessResponse("Password changed successfully",
                    shopkeeperId, null, null, null));
        } catch (RuntimeException e) {
            log.error("Password change failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // ── GET /api/shopkeeper/profile/{shopkeeperId} ────────────────────────────
    @GetMapping("/profile/{shopkeeperId}")
    public ResponseEntity<?> getProfile(@PathVariable Long shopkeeperId) {
        try {
            Shopkeeper sk = shopkeeperService.getShopkeeperById(shopkeeperId);
            return ResponseEntity.ok(new ProfileResponse(sk.getId(), sk.getName(),
                    sk.getPhone(), sk.getShopName(), sk.getAddress(),
                    sk.getEmail(), sk.getLicenseNumber()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // ── Inner Response Classes ────────────────────────────────────────────────

    private static class ErrorResponse {
        private final String error;
        public ErrorResponse(String error) { this.error = error; }
        public String getError() { return error; }
    }

    private static class SuccessResponse {
        private final String message;
        private final Long   shopkeeperId;
        private final String name;
        private final String phone;
        private final String shopName;
        public SuccessResponse(String message, Long shopkeeperId, String name,
                               String phone, String shopName) {
            this.message      = message;
            this.shopkeeperId = shopkeeperId;
            this.name         = name;
            this.phone        = phone;
            this.shopName     = shopName;
        }
        public String getMessage()        { return message; }
        public Long   getShopkeeperId()   { return shopkeeperId; }
        public String getName()           { return name; }
        public String getPhone()          { return phone; }
        public String getShopName()       { return shopName; }
    }

    private static class LoginResponse {
        private final String message;
        private final Long   shopkeeperId;
        private final String name;
        private final String phone;
        private final String shopName;
        private final String address;
        private final String userType;
        public LoginResponse(String message, Long shopkeeperId, String name,
                             String phone, String shopName, String address, String userType) {
            this.message      = message;
            this.shopkeeperId = shopkeeperId;
            this.name         = name;
            this.phone        = phone;
            this.shopName     = shopName;
            this.address      = address;
            this.userType     = userType;
        }
        public String getMessage()        { return message; }
        public Long   getShopkeeperId()   { return shopkeeperId; }
        public String getName()           { return name; }
        public String getPhone()          { return phone; }
        public String getShopName()       { return shopName; }
        public String getAddress()        { return address; }
        public String getUserType()       { return userType; }
    }

    private static class ProfileResponse {
        private final Long   id;
        private final String name;
        private final String phone;
        private final String shopName;
        private final String address;
        private final String email;
        private final String licenseNumber;
        public ProfileResponse(Long id, String name, String phone, String shopName,
                               String address, String email, String licenseNumber) {
            this.id            = id;
            this.name          = name;
            this.phone         = phone;
            this.shopName      = shopName;
            this.address       = address;
            this.email         = email;
            this.licenseNumber = licenseNumber;
        }
        public Long   getId()             { return id; }
        public String getName()           { return name; }
        public String getPhone()          { return phone; }
        public String getShopName()       { return shopName; }
        public String getAddress()        { return address; }
        public String getEmail()          { return email; }
        public String getLicenseNumber()  { return licenseNumber; }
    }
}