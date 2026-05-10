package com.example.farmFeed.service;

import com.example.farmFeed.entity.Order;
import com.example.farmFeed.entity.Farmer;
import com.example.farmFeed.entity.Product;
import com.example.farmFeed.repository.OrderRepository;
import com.example.farmFeed.repository.FarmerRepository;
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

    @Autowired
    private FarmerRepository farmerRepository;

    /**
     * Create new order
     */
    @Transactional
    public Order createOrder(Order order) {
        logger.info("Creating order for farmer: {}", order.getFarmerId());
        
        populateOrderDisplayFields(order);

        Optional<Product> product = productRepository.findById(order.getProductId());
        if (product.isPresent()) {
            // Update product stock
            Product p = product.get();
            p.setStockQuantity(Math.max(0, (p.getStockQuantity() == null ? 0 : p.getStockQuantity()) - order.getQuantity()));
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
        return orderRepository.getPendingOrdersForVendor(vendorId);
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
            o.setStatus(normalizeOrderStatus(status));
            
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

    @Transactional
    public Map<String, Object> createOrdersFromCart(Long farmerId, String deliveryAddress, String paymentMethod, List<Map<String, Object>> cartItems) {
        logger.info("Creating orders from cart for farmer: {} at address: {}", farmerId, deliveryAddress);
        
        Map<String, Object> result = new HashMap<>();
        List<Order> createdOrders = new ArrayList<>();

        for (Map<String, Object> item : cartItems) {
            Long productId = null;
            if (item.get("product_id") != null) {
                productId = ((Number) item.get("product_id")).longValue();
            }
            Long vendorId = null;
            Object vendorObj = item.get("vendor_id");
            if (vendorObj instanceof Number) {
                vendorId = ((Number) vendorObj).longValue();
            } else if (vendorObj != null) {
                String sanitized = vendorObj.toString().replaceAll("[^0-9]", "");
                if (!sanitized.isEmpty()) {
                    vendorId = Long.parseLong(sanitized);
                }
            }

            Integer quantity = item.get("quantity") != null
                    ? ((Number) item.get("quantity")).intValue()
                    : (item.get("qty") != null ? ((Number) item.get("qty")).intValue() : 1);

            Double price = item.get("price") != null
                    ? ((Number) item.get("price")).doubleValue()
                    : 0.0;

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

            populateOrderDisplayFields(order);

            Optional<Product> product = productRepository.findById(productId);
            if (product.isPresent()) {
                Product p = product.get();
                p.setStockQuantity(Math.max(0, (p.getStockQuantity() == null ? 0 : p.getStockQuantity()) - quantity));
                productRepository.save(p);
            }

            Order savedOrder = orderRepository.save(order);
            createdOrders.add(savedOrder);
        }
        
        result.put("success", true);
        result.put("ordersCount", createdOrders.size());
        result.put("orders", createdOrders);
        
        logger.info("Successfully created {} unassigned orders", createdOrders.size());
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
                p.setStockQuantity((p.getStockQuantity() == null ? 0 : p.getStockQuantity()) + o.getQuantity());
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
            o.setStatus("shifting");
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

    /**
     * Get shifting orders for vendor
     */
    @Transactional(readOnly = true)
    public List<Order> getShiftingOrdersForVendor(Long vendorId) {
        logger.info("Fetching shifting orders for vendor: {}", vendorId);
        return orderRepository.getShiftingOrdersByVendor(vendorId);
    }

    /**
     * Vendor accepts order (claims it and updates status to shifting)
     */
    @Transactional
    public Order acceptOrderByVendor(Long orderId, Long vendorId) {
        logger.info("Vendor {} accepting order: {}", vendorId, orderId);
        
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            Order o = order.get();
            o.setVendorId(vendorId);
            o.setStatus("shifting");  // Out for Delivery
            return orderRepository.save(o);
        }
        
        return null;
    }

    private String normalizeOrderStatus(String status) {
        if (status == null || status.isBlank()) {
            return "pending";
        }

        String normalized = status.toLowerCase(Locale.ROOT);
        if ("accepted".equals(normalized) || "shipped".equals(normalized)) {
            return "shifting";
        }

        return normalized;
    }

    private void populateOrderDisplayFields(Order order) {
        Optional<Product> productOpt = productRepository.findById(order.getProductId());
        if (productOpt.isPresent()) {
            Product p = productOpt.get();
            if (order.getProductName() == null || order.getProductName().isBlank()) {
                order.setProductName(p.getName());
            }
            if (order.getProductQuantity() == null) {
                order.setProductQuantity(order.getQuantity());
            }
        }

        Optional<Farmer> farmerOpt = farmerRepository.findById(order.getFarmerId());
        if (farmerOpt.isPresent()) {
            Farmer f = farmerOpt.get();
            if (order.getFarmerName() == null || order.getFarmerName().isBlank()) {
                order.setFarmerName(f.getFullName());
            }
            if (order.getFarmerPhone() == null || order.getFarmerPhone().isBlank()) {
                order.setFarmerPhone(f.getPhone());
            }
            if (order.getFarmerAddress() == null || order.getFarmerAddress().isBlank()) {
                order.setFarmerAddress(f.getAddress());
            }
        }
    }

    /**
     * Get all orders
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        logger.info("Fetching all orders");
        return orderRepository.findAll();
    }

    /**
     * Get orders by status
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(String status) {
        logger.info("Fetching orders with status: {}", status);
        return orderRepository.findByStatus(status);
    }

    /**
     * Get order statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getOrderStatistics() {
        logger.info("Calculating order statistics");
        
        Map<String, Object> stats = new HashMap<>();
        List<Order> allOrders = orderRepository.findAll();
        
        long totalOrders = allOrders.size();
        long pendingOrders = allOrders.stream().filter(o -> "pending".equals(o.getStatus())).count();
        long shiftingOrders = allOrders.stream().filter(o -> "shifting".equals(o.getStatus())).count();
        long deliveredOrders = allOrders.stream().filter(o -> "delivered".equals(o.getStatus())).count();
        long cancelledOrders = allOrders.stream().filter(o -> "cancelled".equals(o.getStatus())).count();
        
        double totalRevenue = allOrders.stream()
                .filter(o -> "delivered".equals(o.getStatus()))
                .mapToDouble(Order::getTotalPrice)
                .sum();
        
        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("shiftingOrders", shiftingOrders);
        stats.put("deliveredOrders", deliveredOrders);
        stats.put("cancelledOrders", cancelledOrders);
        stats.put("totalRevenue", totalRevenue);
        
        return stats;
    }
}
