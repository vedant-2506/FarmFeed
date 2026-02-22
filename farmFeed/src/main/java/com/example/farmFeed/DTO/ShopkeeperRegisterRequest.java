package com.example.farmFeed.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ShopkeeperRegisterRequest — used by POST /api/shopkeeper/register
 *
 * email and licenseNumber are optional (no @NotBlank),
 * but address is required for a shopkeeper.
 */
public class ShopkeeperRegisterRequest {

    @NotBlank(message = "Owner name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Shop name is required")
    @Size(min = 2, max = 200, message = "Shop name must be between 2 and 200 characters")
    private String shopName;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 digits")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    @NotBlank(message = "Address is required")
    private String address;

    private String email;
    private String licenseNumber;

    public ShopkeeperRegisterRequest() {}

    public ShopkeeperRegisterRequest(String name, String shopName, String phone,
                                     String password, String address,
                                     String email, String licenseNumber) {
        this.name          = name;
        this.shopName      = shopName;
        this.phone         = phone;
        this.password      = password;
        this.address       = address;
        this.email         = email;
        this.licenseNumber = licenseNumber;
    }

    public String getName()                    { return name; }
    public void setName(String name)           { this.name = name; }

    public String getShopName()                        { return shopName; }
    public void setShopName(String shopName)           { this.shopName = shopName; }

    public String getPhone()                   { return phone; }
    public void setPhone(String phone)         { this.phone = phone; }

    public String getPassword()                    { return password; }
    public void setPassword(String password)       { this.password = password; }

    public String getAddress()                 { return address; }
    public void setAddress(String address)     { this.address = address; }

    public String getEmail()                   { return email; }
    public void setEmail(String email)         { this.email = email; }

    public String getLicenseNumber()                           { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber)         { this.licenseNumber = licenseNumber; }
}