package com.example.farmFeed.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Column(name = "type", nullable = false)
    private String type; // ORDER_PENDING, ORDER_DELIVERED, LOW_STOCK, ISSUE_REPORTED, etc.

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", length = 2000, nullable = false)
    private String message;

    @Column(name = "related_order_id")
    private Long relatedOrderId;

    @Column(name = "related_product_id")
    private Long relatedProductId;

    @Column(name = "related_vendor_id")
    private Long relatedVendorId;

    @Column(name = "is_read", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isRead;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isRead == null) isRead = false;
    }
}
