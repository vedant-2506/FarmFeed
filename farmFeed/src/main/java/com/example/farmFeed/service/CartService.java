package com.example.farmFeed.service;

import com.example.farmFeed.entity.Cart;
import com.example.farmFeed.entity.Product;
import com.example.farmFeed.repository.CartRepository;
import com.example.farmFeed.repository.ProductRepository;
import com.example.farmFeed.repository.FertilizerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

import java.util.*;

@Service
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FertilizerRepository fertilizerRepository;

    /**
     * Add item to cart
     */
    @Transactional
    public Cart addToCart(Long farmerId, String productId, Long vendorId, Integer quantity) {
        logger.info("Adding product {} to cart for farmer {}", productId, farmerId);
        
        Optional<Cart> existingCart = cartRepository.findByFarmerIdAndProductIdAndVendorId(farmerId, productId, vendorId);
        
        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + quantity);
            return cartRepository.save(cart);
        }
        
        Cart newCart = Cart.builder()
                .farmerId(farmerId)
                .productId(productId)
                .vendorId(vendorId)
                .quantity(quantity)
                .build();
        
        return cartRepository.save(newCart);
    }

    /**
     * Get cart items for farmer
     */
    @Transactional(readOnly = true)
    public List<Cart> getCartItems(Long farmerId) {
        logger.info("Fetching cart items for farmer: {}", farmerId);
        return cartRepository.getCartByFarmer(farmerId);
    }

    /**
     * Get cart with product details
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCartWithDetails(Long farmerId) {
        logger.info("Fetching cart with details for farmer: {}", farmerId);
        
        List<Cart> cartItems = cartRepository.getCartByFarmer(farmerId);
        List<Map<String, Object>> cartDetails = new ArrayList<>();
        
        for (Cart item : cartItems) {
            Optional<Product> product = productRepository.findById(item.getProductId());
            if (product.isPresent()) {
                Product p = product.get();
                Map<String, Object> cartDetail = new HashMap<>();
                cartDetail.put("cartId", item.getId());
                
                // Create a product map with base64 encoded image or URL
                Map<String, Object> productMap = new HashMap<>();
                productMap.put("id", p.getId());
                productMap.put("name", p.getName());
                productMap.put("description", p.getDescription());
                productMap.put("category", p.getCategory());
                productMap.put("price", p.getPrice());
                productMap.put("manufacturer", p.getManufacturer());
                productMap.put("vendor_id", p.getVendorId());
                productMap.put("stock", p.getStock());
                productMap.put("rating", p.getRating());
                productMap.put("total_reviews", p.getTotalReviews());
                
                // Use imageLink directly
                if (p.getImageLink() != null && !p.getImageLink().isEmpty()) {
                    productMap.put("image", p.getImageLink());
                } else {
                    // Try to get image from catalog (FertilizerRepository) by name - much more reliable
                    String imageUrl = fertilizerRepository.findImageByProductName(p.getName());
                    productMap.put("image", imageUrl);
                }
                
                cartDetail.put("product", productMap);
                cartDetail.put("quantity", item.getQuantity());
                cartDetail.put("subtotal", p.getPrice() * item.getQuantity());
                cartDetails.add(cartDetail);
            }
        }
        
        return cartDetails;
    }

    /**
     * Update quantity
     */
    @Transactional
    public Cart updateQuantity(Long cartId, Integer quantity) {
        logger.info("Updating quantity for cart item: {}", cartId);
        
        Optional<Cart> cart = cartRepository.findById(cartId);
        if (cart.isPresent()) {
            Cart c = cart.get();
            c.setQuantity(quantity);
            return cartRepository.save(c);
        }
        
        return null;
    }

    /**
     * Remove item from cart
     */
    @Transactional
    public void removeFromCart(Long cartId) {
        logger.info("Removing item from cart: {}", cartId);
        cartRepository.deleteById(cartId);
    }

    /**
     * Remove product from cart
     */
    @Transactional
    public void removeProductFromCart(Long farmerId, String productId) {
        logger.info("Removing product {} from farmer {}'s cart", productId, farmerId);
        cartRepository.deleteByFarmerIdAndProductId(farmerId, productId);
    }

    /**
     * Clear cart
     */
    @Transactional
    public void clearCart(Long farmerId) {
        logger.info("Clearing cart for farmer: {}", farmerId);
        cartRepository.deleteByFarmerId(farmerId);
    }

    /**
     * Get cart total
     */
    @Transactional(readOnly = true)
    public Double getCartTotal(Long farmerId) {
        logger.info("Calculating cart total for farmer: {}", farmerId);
        
        List<Cart> cartItems = cartRepository.getCartByFarmer(farmerId);
        Double total = 0.0;
        
        for (Cart item : cartItems) {
            Optional<Product> product = productRepository.findById(item.getProductId());
            if (product.isPresent()) {
                total += product.get().getPrice() * item.getQuantity();
            }
        }
        
        return total;
    }

    /**
     * Get cart item count
     */
    @Transactional(readOnly = true)
    public Long getCartItemCount(Long farmerId) {
        logger.info("Fetching item count for farmer's cart: {}", farmerId);
        return cartRepository.getCartCount(farmerId);
    }
}
