package com.example.farmFeed.controller;

import com.example.farmFeed.entity.Cart;
import com.example.farmFeed.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    /**
     * POST /api/cart/add - Add item to cart
     */
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> request) {
        try {
            logger.info("Adding item to cart");
            
            Long farmerId = ((Number) request.get("farmerId")).longValue();
            Long productId = ((Number) request.get("productId")).longValue();
            Long vendorId = ((Number) request.get("vendorId")).longValue();
            Integer quantity = ((Number) request.get("quantity")).intValue();
            
            Cart cartItem = cartService.addToCart(farmerId, productId, vendorId, quantity);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Item added to cart",
                    "cartId", cartItem.getId()
            ));
        } catch (Exception e) {
            logger.error("Error adding to cart: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/cart/farmer/{farmerId} - Get farmer's cart
     */
    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<?> getCart(@PathVariable Long farmerId) {
        try {
            logger.info("Fetching cart for farmer: {}", farmerId);
            List<Cart> cartItems = cartService.getCartItems(farmerId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", cartItems.size(),
                    "data", cartItems
            ));
        } catch (Exception e) {
            logger.error("Error fetching cart: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/cart/farmer/{farmerId}/details - Get cart with product details
     */
    @GetMapping("/farmer/{farmerId}/details")
    public ResponseEntity<?> getCartWithDetails(@PathVariable Long farmerId) {
        try {
            logger.info("Fetching cart details for farmer: {}", farmerId);
            List<Map<String, Object>> cartDetails = cartService.getCartWithDetails(farmerId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", cartDetails.size(),
                    "data", cartDetails
            ));
        } catch (Exception e) {
            logger.error("Error fetching cart details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/cart/farmer/{farmerId}/total - Get cart total
     */
    @GetMapping("/farmer/{farmerId}/total")
    public ResponseEntity<?> getCartTotal(@PathVariable Long farmerId) {
        try {
            logger.info("Calculating cart total for farmer: {}", farmerId);
            Double total = cartService.getCartTotal(farmerId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "total", total
            ));
        } catch (Exception e) {
            logger.error("Error calculating cart total: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/cart/farmer/{farmerId}/count - Get cart item count
     */
    @GetMapping("/farmer/{farmerId}/count")
    public ResponseEntity<?> getCartCount(@PathVariable Long farmerId) {
        try {
            logger.info("Fetching cart count for farmer: {}", farmerId);
            Long count = cartService.getCartItemCount(farmerId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "itemCount", count
            ));
        } catch (Exception e) {
            logger.error("Error fetching cart count: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * PUT /api/cart/{cartId}/quantity - Update quantity
     */
    @PutMapping("/{cartId}/quantity")
    public ResponseEntity<?> updateQuantity(
            @PathVariable Long cartId,
            @RequestBody Map<String, Integer> request) {
        try {
            logger.info("Updating quantity for cart item: {}", cartId);
            
            Integer quantity = request.get("quantity");
            if (quantity == null || quantity <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Invalid quantity"));
            }
            
            Cart updatedCart = cartService.updateQuantity(cartId, quantity);
            
            if (updatedCart == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "error", "Cart item not found"));
            }
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Quantity updated",
                    "data", updatedCart
            ));
        } catch (Exception e) {
            logger.error("Error updating quantity: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/cart/{cartId} - Remove item from cart
     */
    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long cartId) {
        try {
            logger.info("Removing item from cart: {}", cartId);
            
            cartService.removeFromCart(cartId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Item removed from cart"
            ));
        } catch (Exception e) {
            logger.error("Error removing from cart: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/cart/farmer/{farmerId}/product/{productId} - Remove product from cart
     */
    @DeleteMapping("/farmer/{farmerId}/product/{productId}")
    public ResponseEntity<?> removeProductFromCart(
            @PathVariable Long farmerId,
            @PathVariable Long productId) {
        try {
            logger.info("Removing product {} from cart for farmer: {}", productId, farmerId);
            
            cartService.removeProductFromCart(farmerId, productId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Product removed from cart"
            ));
        } catch (Exception e) {
            logger.error("Error removing product from cart: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/cart/farmer/{farmerId}/clear - Clear entire cart
     */
    @DeleteMapping("/farmer/{farmerId}/clear")
    public ResponseEntity<?> clearCart(@PathVariable Long farmerId) {
        try {
            logger.info("Clearing cart for farmer: {}", farmerId);
            
            cartService.clearCart(farmerId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cart cleared successfully"
            ));
        } catch (Exception e) {
            logger.error("Error clearing cart: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
