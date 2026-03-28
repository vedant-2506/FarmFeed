package com.example.farmFeed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.farmFeed.entity.Vendor;
import java.util.Optional;
import java.util.List;
import java.util.Map;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByEmail(String email);
    Optional<Vendor> findByEmailAndPassword(String email, String password);
    Optional<Vendor> findByLicenseNumber(String licenseNumber);
    List<Vendor> findByIsActive(Boolean isActive);
    List<Vendor> findByIsApproved(Boolean isApproved);
    
    @Query("SELECT v FROM Vendor v WHERE v.isActive = true AND v.isApproved = true ORDER BY v.createdAt DESC")
    List<Vendor> getActiveApprovedVendors();
    
    @Query("SELECT v FROM Vendor v WHERE v.isApproved = false ORDER BY v.createdAt DESC")
    List<Vendor> getPendingApprovalVendors();

    @Query("SELECT COUNT(vi) FROM VendorInventory vi WHERE vi.vendorId = :vendorId AND vi.isActive = true")
    Long getVendorInventoryCounts(@Param("vendorId") Long vendorId);

    @Query("SELECT SUM(vi.vendorPrice * vi.quantityInStock) FROM VendorInventory vi WHERE vi.vendorId = :vendorId AND vi.isActive = true")
    Long getVendorInventoryValue(@Param("vendorId") Long vendorId);

    @Query(value = """
        SELECT 
            vi.inventory_id,
            vi.fertilizer_id,
            vi.vendor_price,
            vi.quantity_in_stock,
            bp.product_name as name,
            bp.price_inr as base_price,
            bp.image_link as image_url,
            bp.description_clean as description
        FROM vendor_inventory vi
        LEFT JOIN bighaat_products_raw bp ON CONCAT('bighaat_', vi.fertilizer_id) = bp.mysql_import_key
        WHERE vi.vendor_id = :vendorId AND vi.is_active = true
    """, nativeQuery = true)
    List<Map<String, Object>> getVendorInventoryDetails(@Param("vendorId") Long vendorId);
}
