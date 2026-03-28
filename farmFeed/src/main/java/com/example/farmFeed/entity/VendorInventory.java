package com.example.farmFeed.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * VendorInventory - Links vendors to fertilizers they're selling with custom prices
 */
@Entity
@Table(name = "vendor_inventory", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"vendor_id", "fertilizer_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;  // References shopkeeper table

    @Column(name = "fertilizer_id", nullable = false)
    private Integer fertilizerId;  // References fertilizers table

    @Column(name = "vendor_price", nullable = false)
    private Double vendorPrice;  // Vendor's selling price (markup allowed)

    @Column(name = "quantity_in_stock", nullable = false)
    private Integer quantityInStock;  // Vendor's stock

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) isActive = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
