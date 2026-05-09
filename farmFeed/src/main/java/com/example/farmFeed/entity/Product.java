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
    @Column(name = "product_id")
    private String id;

    @Column(name = "product_name", nullable = false)
    private String name;

    @Column(name = "image_link", length = 2000)
    private String imageLink;

    @Column(name = "primary_category", nullable = false)
    private String category;

    @Column(name = "subcategory")
    private String subcategory;

    @Column(name = "price_inr", nullable = false)
    private Double price;

    @Column(name = "description_clean", length = 2000)
    private String description;

    @Column(name = "detailed_description_10_sentences", length = 6000)
    private String detailedDescription;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    @Column(name = "stock", nullable = false)
    private Integer stock;

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
