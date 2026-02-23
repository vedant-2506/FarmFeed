package com.example.farmFeed.entity;

import jakarta.persistence.*;

/**
 * Farmer — JPA Entity mapped to the "farmer" database table.
 *
 * Registration fields: name, phone, password, address.
 * Email is NOT used for farmer registration or login.
 * Login is phone + password only.
 */
@Entity
@Table(name = "farmer")
public class Farmer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "farmer_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "contact_number", nullable = false, unique = true, length = 15)
    private String phone;

    @Column(name = "address", length = 500)
    private String address;

    // ── Constructors ──────────────────────────────────────────────
    public Farmer() {}

    public Farmer(String name, String phone, String password, String address) {
        this.name     = name;
        this.phone    = phone;
        this.password = password;
        this.address  = address;
    }

    // ── Getters & Setters ─────────────────────────────────────────
    public Long getId()              { return id; }
    public void setId(Long id)       { this.id = id; }

    public String getName()                  { return name; }
    public void setName(String name)         { this.name = name; }

    public String getPassword()                  { return password; }
    public void setPassword(String password)     { this.password = password; }

    public String getPhone()                 { return phone; }
    public void setPhone(String phone)       { this.phone = phone; }

    public String getAddress()               { return address; }
    public void setAddress(String address)   { this.address = address; }

    @Override
    public String toString() {
        return "Farmer{id=" + id + ", name='" + name + "', phone='" + phone + "'}";
    }
}