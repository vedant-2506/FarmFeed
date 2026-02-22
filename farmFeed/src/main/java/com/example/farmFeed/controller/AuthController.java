package com.example.farmFeed.controller;

import com.example.farmFeed.DTO.ChangePasswordRequest;
import com.example.farmFeed.DTO.LoginRequest;
import com.example.farmFeed.DTO.RegisterRequest;
import com.example.farmFeed.entity.Farmer;
/**
 * AuthService - A service class responsible for handling authentication-related business logic.
 * This service provides methods for user authentication, credential validation, token generation,
 * and other security-related operations for the FarmFeed application.
 * 
 * Note: The import cannot be resolved, which suggests:
 * 1. The AuthService class may not exist in the service package
 * 2. The package structure may be incorrect
 * 3. Maven/Gradle dependencies may not be properly configured
 * 4. The project needs to be rebuilt or refreshed
 * 
 * @author FarmFeed Development Team
 * @version 1.0
 */
import com.example.farmFeed.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ============================================================
 * AuthController — Farmer Authentication REST Controller
 * ============================================================
 *
 * BASE URL: /api/auth
 *
 * ┌─────────────────────────────────────────┬────────────────────────────────────┐
 * │ Endpoint                                │ Description                        │
 * ├─────────────────────────────────────────┼────────────────────────────────────┤
 * │ POST /api/auth/register                 │ Register a new farmer              │
 * │ POST /api/auth/login                    │ Farmer login                       │
 * │ PUT  /api/auth/change-password/{id}     │ Change farmer password             │
 * │ GET  /api/auth/profile/{id}             │ Get farmer profile                 │
 * └─────────────────────────────────────────┴────────────────────────────────────┘
 *
 * All responses are JSON. Errors return { "error": "message" }.
 * Success responses return specific response objects (see inner classes).
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ── POST /api/auth/register ───────────────────────────────────────────────
    // Body: { "name":"Ram", "phone":"9876543210", "password":"Test1234!", "address":"Pune" }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            log.info("Farmer registration request for phone: {}", request.getPhone());
            Farmer farmer = authService.registerFarmer(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SuccessResponse("Registration successful",
                            farmer.getId(), farmer.getName(), farmer.getPhone()));
        } catch (RuntimeException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // ── POST /api/auth/login ──────────────────────────────────────────────────
    // Body: { "phone":"9876543210", "password":"Test1234!" }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            log.info("Farmer login for phone: {}", request.getPhone());
            Farmer farmer = authService.authenticateFarmer(request);
            return ResponseEntity.ok(new LoginResponse("Login successful",
                    farmer.getId(), farmer.getName(), farmer.getPhone(),
                    farmer.getAddress(), "FARMER"));
        } catch (RuntimeException e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // ── PUT /api/auth/change-password/{farmerId} ──────────────────────────────
    // Body: { "currentPassword":"...", "newPassword":"...", "confirmPassword":"..." }
    @PutMapping("/change-password/{farmerId}")
    public ResponseEntity<?> changePassword(
            @PathVariable Long farmerId,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            log.info("Password change for farmer ID: {}", farmerId);
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("New password and confirm password do not match"));
            }
            authService.changePassword(farmerId, request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok(new SuccessResponse("Password changed successfully",
                    farmerId, null, null));
        } catch (RuntimeException e) {
            log.error("Password change failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // ── GET /api/auth/profile/{farmerId} ──────────────────────────────────────
    @GetMapping("/profile/{farmerId}")
    public ResponseEntity<?> getProfile(@PathVariable Long farmerId) {
        try {
            Farmer farmer = authService.getFarmerById(farmerId);
            return ResponseEntity.ok(new ProfileResponse(farmer.getId(),
                    farmer.getName(), farmer.getPhone(), farmer.getAddress()));
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
        private final Long   farmerId;
        private final String name;
        private final String phone;
        public SuccessResponse(String message, Long farmerId, String name, String phone) {
            this.message  = message;
            this.farmerId = farmerId;
            this.name     = name;
            this.phone    = phone;
        }
        public String getMessage() { return message; }
        public Long   getFarmerId(){ return farmerId; }
        public String getName()    { return name; }
        public String getPhone()   { return phone; }
    }

    private static class LoginResponse {
        private final String message;
        private final Long   farmerId;
        private final String name;
        private final String phone;
        private final String address;
        private final String userType;
        public LoginResponse(String message, Long farmerId, String name,
                             String phone, String address, String userType) {
            this.message  = message;
            this.farmerId = farmerId;
            this.name     = name;
            this.phone    = phone;
            this.address  = address;
            this.userType = userType;
        }
        public String getMessage()  { return message; }
        public Long   getFarmerId() { return farmerId; }
        public String getName()     { return name; }
        public String getPhone()    { return phone; }
        public String getAddress()  { return address; }
        public String getUserType() { return userType; }
    }

    private static class ProfileResponse {
        private final Long   id;
        private final String name;
        private final String phone;
        private final String address;
        public ProfileResponse(Long id, String name, String phone, String address) {
            this.id      = id;
            this.name    = name;
            this.phone   = phone;
            this.address = address;
        }
        public Long   getId()       { return id; }
        public String getName()     { return name; }
        public String getPhone()    { return phone; }
        public String getAddress()  { return address; }
    }
}

