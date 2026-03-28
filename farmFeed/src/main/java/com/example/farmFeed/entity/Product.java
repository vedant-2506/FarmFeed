package com.example.farmFeed.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "category", nullable = false)
    private String category; // organic, chemical, fertilizer, pesticide, etc.

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] image;

    @Column(name = "rating", columnDefinition = "DECIMAL(3,2) DEFAULT 0")
    private Double rating;

    @Column(name = "total_reviews", columnDefinition = "INT DEFAULT 0")
    private Integer totalReviews;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (rating == null) rating = 0.0;
        if (totalReviews == null) totalReviews = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
