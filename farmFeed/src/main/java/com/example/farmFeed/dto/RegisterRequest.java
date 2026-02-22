package com.example.farmFeed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * RegisterRequest — used by POST /api/auth/register (farmer registration)
 *
 * Validated by @Valid in AuthController.register().
 * address is optional (no @NotBlank).
 */
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 digits")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
    private String password;

    private String address;

    public RegisterRequest() {}

    public RegisterRequest(String name, String phone, String password, String address) {
        this.name     = name;
        this.phone    = phone;
        this.password = password;
        this.address  = address;
    }

    public String getName()                { return name; }
    public void setName(String name)       { this.name = name; }

    public String getPhone()               { return phone; }
    public void setPhone(String phone)     { this.phone = phone; }

    public String getPassword()                  { return password; }
    public void setPassword(String password)     { this.password = password; }

    public String getAddress()               { return address; }
    public void setAddress(String address)   { this.address = address; }
}