package com.example.farmFeed.service;

import com.example.farmFeed.entity.Product;
import com.example.farmFeed.entity.Rating;
import com.example.farmFeed.repository.ProductRepository;
import com.example.farmFeed.repository.RatingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Get all available products
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        logger.info("Fetching all products");
        return productRepository.getAvailableProducts();
    }

    /**
     * Home API-safe fetch that supports both legacy JPA schema and raw SQL product feed schema.
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllProductsForHome() {
        try {
            return fetchProductsFromSqlFeed();
        } catch (Exception ex) {
            logger.warn("SQL feed fetch failed, falling back to JPA schema: {}", ex.getMessage());
            try {
                return productRepository.getAvailableProducts().stream()
                        .map(this::toHomeProduct)
                        .collect(Collectors.toList());
            } catch (Exception fallbackEx) {
                logger.error("JPA fallback for home products also failed: {}", fallbackEx.getMessage());
                return Collections.emptyList();
            }
        }
    }

    /**
     * Debug helper: return all products from repository (ignores stock filter)
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProductsRaw() {
        logger.info("Fetching all products (raw findAll) for debug");
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllProductsRawForHome() {
        try {
            return fetchProductsFromSqlFeed();
        } catch (Exception ex) {
            logger.warn("SQL feed fetch failed, falling back to JPA raw schema: {}", ex.getMessage());
            try {
                return productRepository.findAll().stream()
                        .map(this::toHomeProduct)
                        .collect(Collectors.toList());
            } catch (Exception fallbackEx) {
                logger.error("JPA raw fallback for home products also failed: {}", fallbackEx.getMessage());
                return Collections.emptyList();
            }
        }
    }

    private Map<String, Object> toHomeProduct(Product p) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", p.getId());
        item.put("product_name", p.getName());
        item.put("name", p.getName());
        item.put("description_clean", p.getDescription());
        item.put("description", p.getDescription());
        item.put("primary_category", p.getCategory());
        item.put("price_inr", p.getPrice());
        item.put("price", p.getPrice());
        item.put("rating", p.getRating());
        item.put("manufacturer", p.getManufacturer());
        item.put("vendor_id", p.getVendorId());
        item.put("vendorId", p.getVendorId());
        item.put("stock", p.getStock());
        item.put("total_reviews", p.getTotalReviews());
        item.put("image_link", p.getImageLink());
        return item;
    }

    private List<Map<String, Object>> fetchProductsFromSqlFeed() {
        // Cast columns explicitly to stable JDBC-friendly types to avoid driver type-inference errors
        String sql = "SELECT " +
            "CAST(product_id AS SIGNED) AS product_id, CAST(product_id AS SIGNED) AS id, " +
            "COALESCE(NULLIF(TRIM(product_name), ''), CONCAT('Product #', CAST(product_id AS CHAR))) AS product_name, " +
            "COALESCE(NULLIF(TRIM(product_name), ''), CONCAT('Product #', CAST(product_id AS CHAR))) AS name, " +
            "NULLIF(TRIM(image_link), '') AS image_link, " +
            "COALESCE(NULLIF(TRIM(primary_category), ''), 'Other') AS primary_category, " +
            "NULLIF(TRIM(subcategory), '') AS subcategory, " +
            "CAST(price_inr AS DECIMAL(12,2)) AS price_inr, CAST(price_inr AS DECIMAL(12,2)) AS price, CAST(rating AS DECIMAL(5,2)) AS rating, " +
            "COALESCE(NULLIF(TRIM(CAST(description_clean AS CHAR)), ''), NULLIF(TRIM(detailed_description_10_sentences), ''), CONCAT('FarmFeed product #', CAST(product_id AS CHAR))) AS description_clean, " +
            "COALESCE(NULLIF(TRIM(CAST(description_clean AS CHAR)), ''), NULLIF(TRIM(detailed_description_10_sentences), ''), CONCAT('FarmFeed product #', CAST(product_id AS CHAR))) AS description, " +
            "NULLIF(TRIM(detailed_description_10_sentences), '') AS detailed_description_10_sentences, " +
            "COALESCE(NULLIF(TRIM(manufacturer), ''), 'FarmFeed') AS manufacturer, " +
                "CAST(vendor_id AS SIGNED) AS vendor_id, CAST(stock AS SIGNED) AS stock, CAST(total_reviews AS SIGNED) AS total_reviews, " +
                "CAST(created_at AS CHAR) AS created_at, CAST(updated_at AS CHAR) AS updated_at " +
                "FROM products WHERE COALESCE(stock, 0) > 0 ORDER BY COALESCE(rating, 0) DESC";
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * Get product by ID
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        logger.info("Fetching product with ID: {}", id);
        return productRepository.findById(id);
    }

    /**
     * Get products by vendor
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByVendor(Long vendorId) {
        logger.info("Fetching products for vendor: {}", vendorId);
        return productRepository.findByVendorId(vendorId);
    }

    /**
     * Get products by category
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        logger.info("Fetching products for category: {}", category);
        return productRepository.findByCategory(category);
    }

    /**
     * Smart search for products by name and description
     */
    @Transactional(readOnly = true)
    public List<Product> smartSearch(String keyword) {
        logger.info("Performing smart search for keyword: {}", keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.smartSearch(keyword);
    }

    /**
     * Filter products by category with minimum rating
     */
    @Transactional(readOnly = true)
    public List<Product> filterByCategory(String category, Double minRating) {
        logger.info("Filtering products by category: {} with minRating: {}", category, minRating);
        if (minRating == null) {
            minRating = 0.0;
        }
        return productRepository.filterByCategory(category, minRating);
    }

    /**
     * Get all categories
     */
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        logger.info("Fetching all categories");
        return productRepository.getAllCategories();
    }

    /**
     * Get top-rated products
     */
    @Transactional(readOnly = true)
    public List<Product> getTopRatedProducts(Integer limit) {
        logger.info("Fetching top {} rated products", limit);
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return productRepository.getTopRatedProducts(0.0, PageRequest.of(0, limit));
    }

    /**
     * Compare products
     */
    @Transactional(readOnly = true)
    public Map<String, Object> compareProducts(List<Long> productIds) {
        logger.info("Comparing {} products", productIds.size());

        Map<String, Object> comparison = new HashMap<>();
        List<Product> products = new ArrayList<>();

        for (Long id : productIds) {
            Optional<Product> product = productRepository.findById(id);
            product.ifPresent(products::add);
        }

        comparison.put("products", products);
        comparison.put("count", products.size());

        if (!products.isEmpty()) {
            Optional<Product> cheapest = products.stream()
                    .min(Comparator.comparingDouble(Product::getPrice));
            Optional<Product> expensive = products.stream()
                    .max(Comparator.comparingDouble(Product::getPrice));

            comparison.put("cheapest", cheapest.orElse(null));
            comparison.put("mostExpensive", expensive.orElse(null));

            Optional<Product> bestRated = products.stream()
                    .max(Comparator.comparingDouble(p -> p.getRating() != null ? p.getRating() : 0.0));
            comparison.put("bestRated", bestRated.orElse(null));
        }

        return comparison;
    }

    /**
     * Add a new product
     */
    @Transactional
    public Product addProduct(Product product) {
        logger.info("Adding new product: {}", product.getName());
        return productRepository.save(product);
    }

    /**
     * Update product
     */
    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        logger.info("Updating product: {}", id);

        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();

            if (productDetails.getName() != null) product.setName(productDetails.getName());
            if (productDetails.getDescription() != null) product.setDescription(productDetails.getDescription());
            if (productDetails.getCategory() != null) product.setCategory(productDetails.getCategory());
            if (productDetails.getPrice() != null) product.setPrice(productDetails.getPrice());
            if (productDetails.getStock() != null) product.setStock(productDetails.getStock());
            if (productDetails.getImageLink() != null) product.setImageLink(productDetails.getImageLink());
            if (productDetails.getManufacturer() != null) product.setManufacturer(productDetails.getManufacturer());

            return productRepository.save(product);
        }

        logger.warn("Product not found: {}", id);
        return null;
    }

    /**
     * Delete product
     */
    @Transactional
    public boolean deleteProduct(Long id) {
        logger.info("Deleting product: {}", id);

        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }

        logger.warn("Product not found for deletion: {}", id);
        return false;
    }

    /**
     * Update stock
     */
    @Transactional
    public Product updateStock(Long id, Integer quantity) {
        logger.info("Updating stock for product: {} with quantity: {}", id, quantity);

        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            Product p = product.get();
            p.setStock(p.getStock() + quantity);
            return productRepository.save(p);
        }

        return null;
    }

    /**
     * Get low stock products for a vendor
     */
    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts(Long vendorId, Integer threshold) {
        logger.info("Fetching low stock products for vendor: {} with threshold: {}", vendorId, threshold);
        if (threshold == null || threshold <= 0) {
            threshold = 10;
        }
        return productRepository.getLowStockProducts(vendorId, threshold);
    }

    /**
     * Add rating to product and update product rating
     */
    @Transactional
    public void addRating(Long productId, Integer rating, String review, Long farmerId, Long vendorId) {
        logger.info("Adding rating {} to product: {}", rating, productId);

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            logger.warn("Product not found: {}", productId);
            return;
        }

        Rating newRating = Rating.builder()
                .productId(productId)
                .farmerId(farmerId)
                .vendorId(vendorId)
                .rating(rating)
                .review(review)
                .build();

        ratingRepository.save(newRating);

        Double avgRating = ratingRepository.getAverageRating(productId);
        Long ratingCount = ratingRepository.getRatingCount(productId);

        product.setRating(avgRating != null ? avgRating : 0.0);
        product.setTotalReviews((int)(long)(ratingCount != null ? ratingCount : 0));
        productRepository.save(product);
    }

    /**
     * Get all ratings for a product
     */
    @Transactional(readOnly = true)
    public List<Rating> getProductRatings(Long productId) {
        logger.info("Fetching ratings for product: {}", productId);
        if (productId == null) {
            return Collections.emptyList();
        }
        return ratingRepository.findByProductId(productId);
    }
}

