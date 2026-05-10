package com.example.farmFeed.repository;

import com.example.farmFeed.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(COALESCE(p.category, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> smartSearch(@Param("keyword") String keyword);

    @Query("SELECT p FROM Product p WHERE LOWER(p.category) = LOWER(:category) AND COALESCE(p.rating, 0) >= :minRating ORDER BY COALESCE(p.rating, 0) DESC, p.name ASC")
    List<Product> filterByCategory(@Param("category") String category, @Param("minRating") Double minRating);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL ORDER BY p.category")
    List<String> getAllCategories();

    @Query("SELECT p FROM Product p WHERE COALESCE(p.stockQuantity, 0) > 0 ORDER BY COALESCE(p.rating, 0) DESC, p.name ASC")
    List<Product> getAvailableProducts();

    @Query("SELECT p FROM Product p WHERE COALESCE(p.stockQuantity, 0) <= :threshold ORDER BY COALESCE(p.stockQuantity, 0) ASC, p.name ASC")
    List<Product> getLowStockProducts(@Param("threshold") Integer threshold);

    @Query("SELECT p FROM Product p WHERE COALESCE(p.rating, 0) >= :minRating ORDER BY COALESCE(p.rating, 0) DESC, p.name ASC")
    List<Product> getTopRatedProducts(@Param("minRating") Double minRating, Pageable pageable);
}
