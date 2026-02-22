package com.example.farmFeed.DTO;

import jakarta.validation.constraints.NotBlank;

/**
 * LoginRequest — used by:
 *   POST /api/auth/login         (farmer login)
 *   POST /api/shopkeeper/login   (shopkeeper login)
 *
 * Both farmer and shopkeeper login with phone + password.
 */
public class LoginRequest {

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Password is required")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String phone, String password) {
        this.phone    = phone;
        this.password = password;
    }

    public String getPhone()                 { return phone; }
    public void setPhone(String phone)       { this.phone = phone; }

    public String getPassword()                  { return password; }
    public void setPassword(String password)     { this.password = password; }
}