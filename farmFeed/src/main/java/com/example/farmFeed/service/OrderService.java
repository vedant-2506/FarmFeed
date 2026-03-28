package com.example.farmFeed.service;

import com.example.farmFeed.entity.Order;
import com.example.farmFeed.entity.Product;
import com.example.farmFeed.repository.OrderRepository;
import com.example.farmFeed.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Create new order
     */
    @Transactional
    public Order createOrder(Order order) {
        logger.info("Creating order for farmer: {}", order.getFarmerId());
        
        Optional<Product> product = productRepository.findById(order.getProductId());
        if (product.isPresent()) {
            // Update product stock
            Product p = product.get();
            p.setStock(p.getStock() - order.getQuantity());
            productRepository.save(p);
        }
        
        return orderRepository.save(order);
    }

    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        logger.info("Fetching order: {}", id);
        return orderRepository.findById(id);
    }

    /**
     * Get all orders by farmer
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByFarmer(Long farmerId) {
        logger.info("Fetching orders for farmer: {}", farmerId);
        return orderRepository.getOrderHistoryByFarmer(farmerId);
    }

    /**
     * Get all orders by vendor
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByVendor(Long vendorId) {
        logger.info("Fetching orders for vendor: {}", vendorId);
        return orderRepository.findByVendorId(vendorId);
    }

    /**
     * Get pending orders for vendor
     */
    @Transactional(readOnly = true)
    public List<Order> getPendingOrdersForVendor(Long vendorId) {
        logger.info("Fetching pending orders for vendor: {}", vendorId);
        return orderRepository.getPendingOrdersByVendor(vendorId);
    }

    /**
     * Get pending orders for farmer
     */
    @Transactional(readOnly = true)
    public List<Order> getPendingOrdersForFarmer(Long farmerId) {
        logger.info("Fetching pending orders for farmer: {}", farmerId);
        return orderRepository.getPendingOrdersByFarmer(farmerId);
    }

    /**
     * Update order status
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, String status) {
        logger.info("Updating order {} status to: {}", orderId, status);
        
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            Order o = order.get();
            o.setStatus(status);
            
            if (status.equalsIgnoreCase("delivered")) {
                o.setDeliveryDate(LocalDateTime.now());
            }
            
            return orderRepository.save(o);
        }
        
        logger.warn("Order not found: {}", orderId);
        return null;
    }

    /**
     * Get monthly income for vendor
     */
    @Transactional(readOnly = true)
    public Double getMonthlyIncome(Long vendorId, int year, int month) {
        logger.info("Calculating monthly income for vendor: {} for {}/{}", vendorId, month, year);
        
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        
        Double income = orderRepository.getMonthlyIncomeByVendor(vendorId, startDate, endDate);
        return income != null ? income : 0.0;
    }

    /**
     * Get total income for vendor
     */
    @Transactional(readOnly = true)
    public Double getTotalIncome(Long vendorId) {
        logger.info("Calculating total income for vendor: {}", vendorId);
        
        List<Order> deliveredOrders = orderRepository.getDeliveredOrdersByVendor(vendorId);
        return deliveredOrders.stream()
                .mapToDouble(Order::getTotalPrice)
                .sum();
    }

    /**
     * Get pending order count for vendor
     */
    @Transactional(readOnly = true)
    public Long getPendingOrderCount(Long vendorId) {
        logger.info("Fetching pending order count for vendor: {}", vendorId);
        return orderRepository.getPendingOrderCount(vendorId);
    }

    /**
     * Create multiple orders from cart items (one order per vendor)
     */
    @Transactional
    public Map<String, Object> createOrdersFromCart(Long farmerId, String deliveryAddress, String paymentMethod, List<Map<String, Object>> cartItems) {
        logger.info("Creating orders from cart for farmer: {} at address: {}", farmerId, deliveryAddress);
        
        Map<String, Object> result = new HashMap<>();
        List<Order> createdOrders = new ArrayList<>();
        Map<Long, List<Map<String, Object>>> ordersByVendor = new HashMap<>();
        
        // Group cart items by vendor
        for (Map<String, Object> cartItem : cartItems) {
            Long vendorId = ((Number) cartItem.get("vendor_id")).longValue();
            ordersByVendor.computeIfAbsent(vendorId, k -> new ArrayList<>()).add(cartItem);
        }
        
        // Create one order per vendor
        for (Map.Entry<Long, List<Map<String, Object>>> entry : ordersByVendor.entrySet()) {
            Long vendorId = entry.getKey();
            List<Map<String, Object>> vendorItems = entry.getValue();
            
            for (Map<String, Object> item : vendorItems) {
                Long productId = ((Number) item.get("product_id")).longValue();
                Integer quantity = ((Number) item.get("quantity")).intValue();
                Double price = ((Number) item.get("price")).doubleValue();
                
                Order order = new Order();
                order.setFarmerId(farmerId);
                order.setVendorId(vendorId);
                order.setProductId(productId);
                order.setQuantity(quantity);
                order.setTotalPrice(price * quantity);
                order.setStatus("pending");
                order.setDeliveryAddress(deliveryAddress);
                order.setPaymentMethod(paymentMethod);
                order.setIsPaid(false);
                
                // Update stock
                Optional<Product> product = productRepository.findById(productId);
                if (product.isPresent()) {
                    Product p = product.get();
                    p.setStock(p.getStock() - quantity);
                    productRepository.save(p);
                }
                
                Order savedOrder = orderRepository.save(order);
                createdOrders.add(savedOrder);
            }
        }
        
        result.put("success", true);
        result.put("ordersCount", createdOrders.size());
        result.put("vendorCount", ordersByVendor.size());
        result.put("orders", createdOrders);
        
        logger.info("Successfully created {} orders for {} vendors", createdOrders.size(), ordersByVendor.size());
        return result;
    }

    /**
     * Cancel order
     */
    @Transactional
    public Order cancelOrder(Long orderId) {
        logger.info("Cancelling order: {}", orderId);
        
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            Order o = order.get();
            o.setStatus("cancelled");
            
            // Restore stock
            Optional<Product> product = productRepository.findById(o.getProductId());
            if (product.isPresent()) {
                Product p = product.get();
                p.setStock(p.getStock() + o.getQuantity());
                productRepository.save(p);
            }
            
            return orderRepository.save(o);
        }
        
        return null;
    }

    /**
     * Get all undelivered orders (for admin tracking)
     */
    @Transactional(readOnly = true)
    public List<Order> getAllUndeliveredOrders() {
        logger.info("Fetching all undelivered orders");
        return orderRepository.getAllUndeliveredOrders();
    }

    /**
     * Update tracking number
     */
    @Transactional
    public Order updateTrackingNumber(Long orderId, String trackingNumber) {
        logger.info("Updating tracking number for order: {} to: {}", orderId, trackingNumber);
        
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            Order o = order.get();
            o.setTrackingNumber(trackingNumber);
            o.setStatus("shipped");
            return orderRepository.save(o);
        }
        
        return null;
    }

    /**
     * Mark order as paid
     */
    @Transactional
    public Order markOrderAsPaid(Long orderId) {
        logger.info("Marking order {} as paid", orderId);
        
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            Order o = order.get();
            o.setIsPaid(true);
            return orderRepository.save(o);
        }
        
        return null;
    }
}
