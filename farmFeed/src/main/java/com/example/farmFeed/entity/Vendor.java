package com.example.farmFeed.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "shopkeeper")
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "shop_name", nullable = false)
    private String shopName;

    @Column(name = "licence_number", nullable = false, unique = true)
    private String licenseNumber;

    @Column(name = "shop_address", nullable = false)
    private String shopAddress;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // Constructors
    public Vendor() {}

    public Vendor(String ownerName, String shopName, String licenseNumber, 
                  String shopAddress, String email, String password) {
        this.ownerName = ownerName;
        this.shopName = shopName;
        this.licenseNumber = licenseNumber;
        this.shopAddress = shopAddress;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Vendor{" +
                "id=" + id +
                ", ownerName='" + ownerName + '\'' +
                ", shopName='" + shopName + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", shopAddress='" + shopAddress + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
