package com.example.farmFeed.repository;

import com.example.farmFeed.entity.VendorInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendorInventoryRepository extends JpaRepository<VendorInventory, Long> {

    /**
     * Get all inventory items for a specific vendor
     */
    List<VendorInventory> findByVendorIdAndIsActiveTrue(Long vendorId);

    /**
     * Get a specific inventory item for a vendor
     */
    Optional<VendorInventory> findByVendorIdAndFertilizerId(Long vendorId, Integer fertilizerId);

    /**
     * Check if vendor already has this fertilizer in inventory
     */
    boolean existsByVendorIdAndFertilizerId(Long vendorId, Integer fertilizerId);

    /**
     * Get all vendors selling a specific fertilizer
     */
    List<VendorInventory> findByFertilizerIdAndIsActiveTrue(Integer fertilizerId);

    /**
     * Get deleted inventory items (soft delete)
     */
    List<VendorInventory> findByVendorIdAndIsActiveFalse(Long vendorId);
}
