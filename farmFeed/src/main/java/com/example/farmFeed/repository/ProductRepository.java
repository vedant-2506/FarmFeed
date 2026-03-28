package com.example.farmFeed.repository;

import com.example.farmFeed.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    List<Product> findByVendorId(Long vendorId);

    List<Product> findByCategory(String category);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> smartSearch(@Param("keyword") String keyword);

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.rating >= :minRating ORDER BY p.rating DESC")
    List<Product> filterByCategory(@Param("category") String category, @Param("minRating") Double minRating);

    @Query("SELECT p FROM Product p WHERE p.stock > 0 ORDER BY p.rating DESC")
    List<Product> getAvailableProducts();

    @Query("SELECT p FROM Product p WHERE p.vendorId = :vendorId AND p.stock <= :threshold")
    List<Product> getLowStockProducts(@Param("vendorId") Long vendorId, @Param("threshold") Integer threshold);

    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> getAllCategories();

    @Query("SELECT p FROM Product p WHERE p.rating >= :minRating ORDER BY p.rating DESC LIMIT :limit")
    List<Product> getTopRatedProducts(@Param("minRating") Double minRating, @Param("limit") Integer limit);
}
