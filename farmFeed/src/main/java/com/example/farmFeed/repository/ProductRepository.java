package com.example.farmFeed.repository;

import com.example.farmFeed.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    Optional<Product> findByName(String name);

    List<Product> findByVendorId(Long vendorId);

    List<Product> findByCategory(String category);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> smartSearch(@Param("keyword") String keyword);

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.rating >= :minRating ORDER BY p.rating DESC")
    List<Product> filterByCategory(@Param("category") String category, @Param("minRating") Double minRating);

    @Query(value = """
            SELECT
                product_id AS product_id,
                product_name AS product_name,
                image_link AS image_link,
                primary_category AS primary_category,
                subcategory AS subcategory,
                price_inr AS price_inr,
                rating AS rating,
                description_clean AS description_clean,
                detailed_description_10_sentences AS detailed_description_10_sentences,
                manufacturer AS manufacturer,
                vendor_id AS vendor_id,
                stock AS stock,
                total_reviews AS total_reviews,
                created_at AS created_at,
                updated_at AS updated_at
            FROM products
            WHERE COALESCE(stock, 0) > 0
            ORDER BY COALESCE(rating, 0) DESC
            """, nativeQuery = true)
    List<Product> getAvailableProducts();

    @Query("SELECT p FROM Product p WHERE p.vendorId = :vendorId AND p.stock <= :threshold")
    List<Product> getLowStockProducts(@Param("vendorId") Long vendorId, @Param("threshold") Integer threshold);

    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> getAllCategories();

    @Query("SELECT p FROM Product p WHERE p.rating >= :minRating ORDER BY p.rating DESC")
    List<Product> getTopRatedProducts(@Param("minRating") Double minRating, Pageable pageable);
}
