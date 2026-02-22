package com.example.farmFeed.entity;

import jakarta.persistence.*;

/**
 * ============================================================
 * Shopkeeper — JPA Entity mapped to the "shopkeeper" database table
 * ============================================================
 *
 * DATABASE TABLE: shopkeeper
 * ┌────────────────┬──────────────────┬──────────────────────────────┐
 * │ Java Field     │ DB Column        │ Notes                        │
 * ├────────────────┼──────────────────┼──────────────────────────────┤
 * │ shopkeeperId   │ shopkeeper_id    │ Primary Key, Auto Increment  │
 * │ name           │ name             │ Owner name, NOT NULL         │
 * │ shopName       │ shop_name        │ NOT NULL                     │
 * │ phone          │ contact_number   │ NOT NULL, UNIQUE             │
 * │ password       │ password         │ BCrypt hash, NOT NULL        │
 * │ address        │ address          │ NOT NULL                     │
 * │ email          │ email            │ Optional                     │
 * │ licenseNumber  │ license_number   │ Optional                     │
 * └────────────────┴──────────────────┴──────────────────────────────┘
 *
 * BUG FIXED:
 *   - @Table(name = "shopkeepers") → @Table(name = "shopkeeper")
 *     The SQL schema creates the table as "shopkeeper" (no 's').
 *     With the wrong name, JPA would try to create a second table
 *     "shopkeepers" and all queries would fail on MySQL.
 */
@Entity
@Table(name = "shopkeeper")
public class Shopkeeper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shopkeeper_id")
    private Long shopkeeperId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "shop_name", nullable = false, length = 200)
    private String shopName;

    // Field named "phone" so findByPhone() / existsByPhone() work in repository
    @Column(name = "contact_number", nullable = false, unique = true, length = 15)
    private String phone;

    // Stores BCrypt hash — must be VARCHAR(100) in DB
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "address", nullable = false, length = 500)
    private String address;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "license_number", length = 50)
    private String licenseNumber;

    // ── Constructors ─────────────────────────────────────────────────────────

    public Shopkeeper() {}

    public Shopkeeper(String name, String shopName, String phone,
                      String password, String address) {
        this.name     = name;
        this.shopName = shopName;
        this.phone    = phone;
        this.password = password;
        this.address  = address;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    // getId() returns shopkeeperId — consistent with how controllers use it
    public Long getId()            { return shopkeeperId; }
    public void setId(Long id)     { this.shopkeeperId = id; }

    public String getName()                  { return name; }
    public void setName(String name)         { this.name = name; }

    public String getShopName()                      { return shopName; }
    public void setShopName(String shopName)         { this.shopName = shopName; }

    public String getPhone()                 { return phone; }
    public void setPhone(String phone)       { this.phone = phone; }

    public String getPassword()                  { return password; }
    public void setPassword(String password)     { this.password = password; }

    public String getAddress()               { return address; }
    public void setAddress(String address)   { this.address = address; }

    public String getEmail()                 { return email; }
    public void setEmail(String email)       { this.email = email; }

    public String getLicenseNumber()                         { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber)       { this.licenseNumber = licenseNumber; }

    // ── toString

    @Override
    public String toString() {
        return "Shopkeeper{id=" + shopkeeperId + ", name='" + name
                + "', shopName='" + shopName + "', phone='" + phone
                + "', address='" + address + "'}";
    }
}