package com.example.farmFeed.repository;

import com.example.farmFeed.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByFarmerId(Long farmerId);

    Optional<Cart> findByFarmerIdAndProductIdAndVendorId(Long farmerId, Long productId, Long vendorId);

    @Query("SELECT c FROM Cart c WHERE c.farmerId = :farmerId ORDER BY c.addedAt DESC")
    List<Cart> getCartByFarmer(@Param("farmerId") Long farmerId);

    void deleteByFarmerId(Long farmerId);

    void deleteByFarmerIdAndProductId(Long farmerId, Long productId);

    @Query("SELECT COUNT(c) FROM Cart c WHERE c.farmerId = :farmerId")
    Long getCartCount(@Param("farmerId") Long farmerId);
}
