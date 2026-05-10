package com.example.farmFeed.service;

import com.example.farmFeed.entity.Product;
import com.example.farmFeed.entity.Rating;
import com.example.farmFeed.repository.ProductRepository;
import com.example.farmFeed.repository.RatingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * Get all available products
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        logger.info("Fetching all products");
        return productRepository.findAll(Sort.by(Sort.Order.desc("rating"), Sort.Order.asc("name")));
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
        logger.info("Fetching products for vendor inventory: {}", vendorId);
        return productRepository.getAvailableProducts();
    }

    /**
     * Get products by category
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        logger.info("Fetching products for category: {}", category);
        if (category == null || category.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.findByCategoryIgnoreCase(category.trim());
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
     * Search products by keyword.
     */
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String keyword) {
        return smartSearch(keyword);
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
        return productRepository.getTopRatedProducts(0.0, PageRequest.of(0, limit, Sort.by(Sort.Order.desc("rating"), Sort.Order.asc("name"))));
    }

    /**
     * Bulk insert products with duplicate protection.
     */
    @Transactional
    public Map<String, Object> bulkUploadProducts(List<Product> products) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (products == null || products.isEmpty()) {
            result.put("inserted", 0);
            result.put("skipped", 0);
            result.put("duplicates", List.of());
            result.put("data", List.of());
            return result;
        }

        List<Product> toSave = new ArrayList<>();
        List<String> duplicates = new ArrayList<>();

        for (Product product : products) {
            Product sanitized = sanitizeProduct(product);
            if (sanitized.getName() == null || sanitized.getName().isBlank()) {
                continue;
            }

            if (productRepository.existsByNameIgnoreCase(sanitized.getName())) {
                duplicates.add(sanitized.getName());
                continue;
            }

            toSave.add(sanitized);
        }

        List<Product> saved = productRepository.saveAll(toSave);

        result.put("inserted", saved.size());
        result.put("skipped", products.size() - saved.size());
        result.put("duplicates", duplicates);
        result.put("data", saved);
        return result;
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
        Product sanitized = sanitizeProduct(product);
        logger.info("Adding new product: {}", sanitized.getName());

        if (productRepository.existsByNameIgnoreCase(sanitized.getName())) {
            throw new IllegalArgumentException("Product with the same name already exists");
        }

        return productRepository.save(sanitized);
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

            if (productDetails.getName() != null) product.setName(productDetails.getName().trim());
            if (productDetails.getDescription() != null) product.setDescription(productDetails.getDescription().trim());
            if (productDetails.getCategory() != null) product.setCategory(productDetails.getCategory().trim());
            if (productDetails.getSubcategory() != null) product.setSubcategory(productDetails.getSubcategory().trim());
            if (productDetails.getPrice() != null) product.setPrice(productDetails.getPrice());
            if (productDetails.getStockQuantity() != null) product.setStockQuantity(productDetails.getStockQuantity());
            if (productDetails.getImageUrl() != null) product.setImageUrl(productDetails.getImageUrl().trim());
            if (productDetails.getRating() != null) product.setRating(productDetails.getRating());
            if (productDetails.getTotalReviews() != null) product.setTotalReviews(productDetails.getTotalReviews());

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
            p.setStockQuantity(Math.max(0, (p.getStockQuantity() == null ? 0 : p.getStockQuantity()) + quantity));
            return productRepository.save(p);
        }

        return null;
    }

    /**
     * Get low stock products for a vendor
     */
    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts(Long vendorId, Integer threshold) {
        logger.info("Fetching low stock products with threshold: {}", threshold);
        if (threshold == null || threshold <= 0) {
            threshold = 10;
        }
        return productRepository.getLowStockProducts(threshold);
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

    private Product sanitizeProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product payload is required");
        }

        if (product.getName() != null) {
            product.setName(product.getName().trim());
        }
        if (product.getCategory() != null) {
            product.setCategory(product.getCategory().trim());
        }
        if (product.getSubcategory() != null) {
            product.setSubcategory(product.getSubcategory().trim());
        }
        if (product.getDescription() != null) {
            product.setDescription(product.getDescription().trim());
        }
        if (product.getImageUrl() != null) {
            product.setImageUrl(product.getImageUrl().trim());
        }

        if (product.getName() == null || product.getName().isBlank()) {
            product.setName("Unnamed Product");
        }
        if (product.getCategory() == null || product.getCategory().isBlank()) {
            product.setCategory("General");
        }
        if (product.getDescription() == null || product.getDescription().isBlank()) {
            product.setDescription("No description available");
        }
        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        if (product.getStockQuantity() == null || product.getStockQuantity() < 0) {
            product.setStockQuantity(0);
        }
        if (product.getRating() == null) {
            product.setRating(0.0);
        }
        if (product.getTotalReviews() == null) {
            product.setTotalReviews(0);
        }

        return product;
    }
}

