package com.example.farmFeed.entity;
 
import com.fasterxml.jackson.annotation.JsonAlias;
 
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

    @NotBlank(message = "Product name is required")
    @JsonAlias({"productName", "product_name"})
    @Column(name = "product_name")
    private String name;

    @Column(name = "image_link", length = 2000)
    private String imageLink;

    @NotBlank(message = "Category is required")
    @JsonAlias({"primary_category", "categoryName"})
    @Column(name = "primary_category")
    private String category;

    @Column(name = "subcategory")
    private String subcategory;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @JsonAlias({"price_inr", "productPrice"})
    @Column(name = "price_inr")
    private Double price;

    @Column(name = "rating")
    private Double rating;

    @NotBlank(message = "Description is required")
    @JsonAlias({"description_clean", "productDescription"})
    @Column(name = "description_clean", columnDefinition = "TEXT")
    private String description;

    @Column(name = "detailed_description_10_sentences", columnDefinition = "TEXT")
    private String detailedDescription;

    @Column(name = "manufacturer")
    private String manufacturer;

    @NotNull(message = "Vendor ID is required")
    @JsonAlias({"vendor_id", "vendorId"})
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
        // Set defaults for required fields if null or empty
        if (name == null || name.trim().isEmpty()) name = "Unnamed Product";
        if (category == null || category.trim().isEmpty()) category = "General";
        if (price == null || price <= 0) price = 0.0; // But validation prevents this
        if (description == null || description.trim().isEmpty()) description = "No description available";
        if (vendorId == null) vendorId = 1L; // Default vendor, but validation prevents this
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
