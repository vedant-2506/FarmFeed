package com.example.farmFeed.repository;

import com.example.farmFeed.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByProductId(Long productId);

    List<Rating> findByFarmerId(Long farmerId);

    Optional<Rating> findByProductIdAndFarmerId(Long productId, Long farmerId);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.productId = :productId")
    Double getAverageRating(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.productId = :productId")
    Long getRatingCount(@Param("productId") Long productId);

    @Query("SELECT r FROM Rating r WHERE r.vendorId = :vendorId ORDER BY r.createdAt DESC")
    List<Rating> getRatingsByVendor(@Param("vendorId") Long vendorId);

    @Query("SELECT r FROM Rating r WHERE r.productId = :productId AND r.rating >= :minRating ORDER BY r.createdAt DESC")
    List<Rating> getFileredRatings(@Param("productId") Long productId, @Param("minRating") Integer minRating);
}
