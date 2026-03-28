package com.example.farmFeed.controller;

import com.example.farmFeed.entity.Product;
import com.example.farmFeed.entity.Rating;
import com.example.farmFeed.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    /**
     * GET /api/products - Get all available products
     */
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        try {
            logger.info("Fetching all products");
            List<Product> products = productService.getAllProducts();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", products.size(),
                    "data", products
            ));
        } catch (Exception e) {
            logger.error("Error fetching products: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/products/{id} - Get product details
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            logger.info("Fetching product: {}", id);
            Optional<Product> product = productService.getProductById(id);
            
            if (product.isPresent()) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "data", product.get()
                ));
            }
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "error", "Product not found"));
        } catch (Exception e) {
            logger.error("Error fetching product {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/products/search?keyword=keyword - Smart search
     */
    @GetMapping("/search/smart")
    public ResponseEntity<?> smartSearch(@RequestParam String keyword) {
        try {
            logger.info("Smart searching for: {}", keyword);
            List<Product> results = productService.smartSearch(keyword);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", results.size(),
                    "data", results
            ));
        } catch (Exception e) {
            logger.error("Error in smart search: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/products/filter?category=organic&minRating=3
     */
    @GetMapping("/filter")
    public ResponseEntity<?> filterProducts(
            @RequestParam String category,
            @RequestParam(required = false) Double minRating) {
        try {
            logger.info("Filtering products by category: {}, minRating: {}", category, minRating);
            List<Product> results = productService.filterByCategory(category, minRating);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", results.size(),
                    "data", results
            ));
        } catch (Exception e) {
            logger.error("Error filtering products: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/products/categories - Get all categories
     */
    @GetMapping("/categories/all")
    public ResponseEntity<?> getAllCategories() {
        try {
            logger.info("Fetching all categories");
            List<String> categories = productService.getAllCategories();
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", categories.size(),
                    "data", categories
            ));
        } catch (Exception e) {
            logger.error("Error fetching categories: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/products/category/{category} - Get products by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getByCategory(@PathVariable String category) {
        try {
            logger.info("Fetching products for category: {}", category);
            List<Product> results = productService.getProductsByCategory(category);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", results.size(),
                    "data", results
            ));
        } catch (Exception e) {
            logger.error("Error fetching category products: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/products/top-rated - Get top rated products
     */
    @GetMapping("/top-rated")
    public ResponseEntity<?> getTopRated(@RequestParam(required = false) Integer limit) {
        try {
            logger.info("Fetching top rated products");
            List<Product> results = productService.getTopRatedProducts(limit);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", results.size(),
                    "data", results
            ));
        } catch (Exception e) {
            logger.error("Error fetching top rated products: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * POST /api/products/compare - Compare products
     */
    @PostMapping("/compare")
    public ResponseEntity<?> compareProducts(@RequestBody Map<String, Object> request) {
        try {
            logger.info("Comparing products");
            
            @SuppressWarnings("unchecked")
            List<Long> productIds = (List<Long>) request.get("productIds");
            
            if (productIds == null || productIds.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "No product IDs provided"));
            }
            
            Map<String, Object> comparison = productService.compareProducts(productIds);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", comparison
            ));
        } catch (Exception e) {
            logger.error("Error comparing products: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/products/vendor/{vendorId} - Get products by vendor
     */
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<?> getByVendor(@PathVariable Long vendorId) {
        try {
            logger.info("Fetching products for vendor: {}", vendorId);
            List<Product> results = productService.getProductsByVendor(vendorId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", results.size(),
                    "data", results
            ));
        } catch (Exception e) {
            logger.error("Error fetching vendor products: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * POST /api/products - Add new product (vendor only)
     */
    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        try {
            logger.info("Adding new product: {}", product.getName());
            
            Product savedProduct = productService.addProduct(product);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Product added successfully",
                    "data", savedProduct
            ));
        } catch (Exception e) {
            logger.error("Error adding product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * PUT /api/products/{id} - Update product (vendor only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        try {
            logger.info("Updating product: {}", id);
            
            Product updatedProduct = productService.updateProduct(id, productDetails);
            
            if (updatedProduct == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "error", "Product not found"));
            }
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Product updated successfully",
                    "data", updatedProduct
            ));
        } catch (Exception e) {
            logger.error("Error updating product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/products/{id} - Delete product (vendor only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            logger.info("Deleting product: {}", id);
            
            boolean deleted = productService.deleteProduct(id);
            
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "error", "Product not found"));
            }
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Product deleted successfully"
            ));
        } catch (Exception e) {
            logger.error("Error deleting product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * POST /api/products/{id}/rating - Add rating/review
     */
    @PostMapping("/{productId}/rating")
    public ResponseEntity<?> addRating(
            @PathVariable Long productId,
            @RequestBody Map<String, Object> ratingData) {
        try {
            logger.info("Adding rating for product: {}", productId);
            
            Integer rating = ((Number) ratingData.get("rating")).intValue();
            String review = (String) ratingData.get("review");
            Long farmerId = ((Number) ratingData.get("farmerId")).longValue();
            Long vendorId = ((Number) ratingData.get("vendorId")).longValue();
            
            productService.addRating(productId, rating, review, farmerId, vendorId);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Rating added successfully"
            ));
        } catch (Exception e) {
            logger.error("Error adding rating: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/products/{id}/ratings - Get all ratings for product
     */
    @GetMapping("/{productId}/ratings")
    public ResponseEntity<?> getProductRatings(@PathVariable Long productId) {
        try {
            logger.info("Fetching ratings for product: {}", productId);
            List<Rating> ratings = productService.getProductRatings(productId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", ratings.size(),
                    "data", ratings
            ));
        } catch (Exception e) {
            logger.error("Error fetching ratings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
