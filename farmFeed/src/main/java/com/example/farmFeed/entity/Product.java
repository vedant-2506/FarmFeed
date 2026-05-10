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

    @Column(name = "product_name")
    private String name;

    @Column(name = "image_link", length = 2000)
    private String imageLink;

    @Column(name = "primary_category")
    private String category;

    @Column(name = "subcategory")
    private String subcategory;

    @Column(name = "price_inr")
    private Double price;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "description_clean", columnDefinition = "TEXT")
    private String description;

    @Column(name = "detailed_description_10_sentences", columnDefinition = "TEXT")
    private String detailedDescription;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "vendor_id")
    private Long vendorId;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "total_reviews")
    private Integer totalReviews;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (rating == null) rating = 0.0;
        if (totalReviews == null) totalReviews = 0;
        if (stock == null) stock = 100;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
