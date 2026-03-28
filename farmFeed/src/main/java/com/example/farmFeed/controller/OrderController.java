package com.example.farmFeed.controller;

import com.example.farmFeed.entity.Order;
import com.example.farmFeed.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    /**
     * POST /api/orders - Create new order
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            logger.info("Creating new order for farmer: {}", order.getFarmerId());
            
            Order createdOrder = orderService.createOrder(order);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Order created successfully",
                    "orderId", createdOrder.getId(),
                    "data", createdOrder
            ));
        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/{id} - Get order details
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            logger.info("Fetching order: {}", id);
            Optional<Order> order = orderService.getOrderById(id);
            
            if (order.isPresent()) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "data", order.get()
                ));
            }
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "error", "Order not found"));
        } catch (Exception e) {
            logger.error("Error fetching order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/farmer/{farmerId} - Get farmer's orders (order tracking)
     */
    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<?> getFarmerOrders(@PathVariable Long farmerId) {
        try {
            logger.info("Fetching orders for farmer: {}", farmerId);
            List<Order> orders = orderService.getOrdersByFarmer(farmerId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", orders.size(),
                    "data", orders
            ));
        } catch (Exception e) {
            logger.error("Error fetching farmer orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/farmer/{farmerId}/pending - Get farmer's pending orders
     */
    @GetMapping("/farmer/{farmerId}/pending")
    public ResponseEntity<?> getPendingFarmerOrders(@PathVariable Long farmerId) {
        try {
            logger.info("Fetching pending orders for farmer: {}", farmerId);
            List<Order> orders = orderService.getPendingOrdersForFarmer(farmerId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", orders.size(),
                    "data", orders
            ));
        } catch (Exception e) {
            logger.error("Error fetching pending orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/vendor/{vendorId} - Get vendor's orders
     */
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<?> getVendorOrders(@PathVariable Long vendorId) {
        try {
            logger.info("Fetching orders for vendor: {}", vendorId);
            List<Order> orders = orderService.getOrdersByVendor(vendorId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", orders.size(),
                    "data", orders
            ));
        } catch (Exception e) {
            logger.error("Error fetching vendor orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/vendor/{vendorId}/pending - Get vendor's pending orders
     */
    @GetMapping("/vendor/{vendorId}/pending")
    public ResponseEntity<?> getPendingVendorOrders(@PathVariable Long vendorId) {
        try {
            logger.info("Fetching pending orders for vendor: {}", vendorId);
            List<Order> orders = orderService.getPendingOrdersForVendor(vendorId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", orders.size(),
                    "data", orders
            ));
        } catch (Exception e) {
            logger.error("Error fetching pending vendor orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * PUT /api/orders/{id}/status - Update order status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            logger.info("Updating status for order: {}", id);
            
            String status = request.get("status");
            if (status == null || status.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Status is required"));
            }
            
            Order updatedOrder = orderService.updateOrderStatus(id, status);
            
            if (updatedOrder == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "error", "Order not found"));
            }
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Order status updated",
                    "data", updatedOrder
            ));
        } catch (Exception e) {
            logger.error("Error updating order status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * PUT /api/orders/{id}/tracking - Update tracking number
     */
    @PutMapping("/{id}/tracking")
    public ResponseEntity<?> updateTrackingNumber(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            logger.info("Updating tracking for order: {}", id);
            
            String trackingNumber = request.get("trackingNumber");
            if (trackingNumber == null || trackingNumber.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Tracking number is required"));
            }
            
            Order updatedOrder = orderService.updateTrackingNumber(id, trackingNumber);
            
            if (updatedOrder == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "error", "Order not found"));
            }
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Tracking number updated",
                    "data", updatedOrder
            ));
        } catch (Exception e) {
            logger.error("Error updating tracking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * POST /api/orders/{id}/cancel - Cancel order
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            logger.info("Cancelling order: {}", id);
            
            Order cancelledOrder = orderService.cancelOrder(id);
            
            if (cancelledOrder == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "error", "Order not found"));
            }
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Order cancelled successfully",
                    "data", cancelledOrder
            ));
        } catch (Exception e) {
            logger.error("Error cancelling order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * POST /api/orders/{id}/pay - Mark order as paid
     */
    @PostMapping("/{id}/pay")
    public ResponseEntity<?> markAsPaid(@PathVariable Long id) {
        try {
            logger.info("Marking order as paid: {}", id);
            
            Order paidOrder = orderService.markOrderAsPaid(id);
            
            if (paidOrder == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "error", "Order not found"));
            }
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Order marked as paid",
                    "data", paidOrder
            ));
        } catch (Exception e) {
            logger.error("Error marking order as paid: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/vendor/{vendorId}/income/monthly - Get monthly income
     */
    @GetMapping("/vendor/{vendorId}/income/monthly")
    public ResponseEntity<?> getMonthlyIncome(
            @PathVariable Long vendorId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        try {
            logger.info("Fetching monthly income for vendor: {} for {}/{}", vendorId, month, year);
            
            Double income = orderService.getMonthlyIncome(vendorId, year, month);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "income", income,
                    "month", month,
                    "year", year
            ));
        } catch (Exception e) {
            logger.error("Error fetching monthly income: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/vendor/{vendorId}/income/total - Get total income
     */
    @GetMapping("/vendor/{vendorId}/income/total")
    public ResponseEntity<?> getTotalIncome(@PathVariable Long vendorId) {
        try {
            logger.info("Fetching total income for vendor: {}", vendorId);
            
            Double totalIncome = orderService.getTotalIncome(vendorId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "totalIncome", totalIncome
            ));
        } catch (Exception e) {
            logger.error("Error fetching total income: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/vendor/{vendorId}/pending-count - Get pending order count
     */
    @GetMapping("/vendor/{vendorId}/pending-count")
    public ResponseEntity<?> getPendingCount(@PathVariable Long vendorId) {
        try {
            logger.info("Fetching pending count for vendor: {}", vendorId);
            
            Long count = orderService.getPendingOrderCount(vendorId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "pendingCount", count
            ));
        } catch (Exception e) {
            logger.error("Error fetching pending count: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * POST /api/orders/place - Farmer places order(s) from cart
     * Creates one order per vendor if cart has items from multiple vendors
     * Request body should contain: farmerId, deliveryAddress, paymentMethod, cartItems
     */
    @PostMapping("/place")
    public ResponseEntity<?> placeOrdersFromCart(@RequestBody Map<String, Object> request) {
        try {
            logger.info("Processing order placement from cart");
            
            Long farmerId = ((Number) request.get("farmerId")).longValue();
            String deliveryAddress = (String) request.get("deliveryAddress");
            String paymentMethod = (String) request.get("paymentMethod");
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cartItems = (List<Map<String, Object>>) request.get("cartItems");
            
            if (farmerId == null || deliveryAddress == null || deliveryAddress.isEmpty() || cartItems == null || cartItems.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Farmer ID, delivery address, and cart items are required"));
            }
            
            Map<String, Object> result = orderService.createOrdersFromCart(farmerId, deliveryAddress, paymentMethod, cartItems);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            logger.error("Error placing orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * POST /api/orders/{id}/accept - Vendor accepts order
     * Updates status from PENDING to ACCEPTED
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<?> acceptOrder(@PathVariable Long id) {
        try {
            logger.info("Vendor accepting order: {}", id);
            
            Order acceptedOrder = orderService.updateOrderStatus(id, "accepted");
            
            if (acceptedOrder == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "error", "Order not found"));
            }
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Order accepted successfully",
                    "data", acceptedOrder
            ));
        } catch (Exception e) {
            logger.error("Error accepting order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * POST /api/orders/{id}/reject - Vendor rejects order
     * Updates status from PENDING to REJECTED
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectOrder(@PathVariable Long id) {
        try {
            logger.info("Vendor rejecting order: {}", id);
            
            Order rejectedOrder = orderService.updateOrderStatus(id, "rejected");
            
            if (rejectedOrder == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "error", "Order not found"));
            }
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Order rejected successfully",
                    "data", rejectedOrder
            ));
        } catch (Exception e) {
            logger.error("Error rejecting order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * POST /api/orders/{id}/ship - Vendor ships order
     * Status changes from ACCEPTED to SHIPPED
     * Requires tracking number in request body
     */
    @PostMapping("/{id}/ship")
    public ResponseEntity<?> shipOrder(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            logger.info("Shipping order: {}", id);
            
            String trackingNumber = request.get("trackingNumber");
            if (trackingNumber == null || trackingNumber.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Tracking number is required for shipment"));
            }
            
            Order shippedOrder = orderService.updateTrackingNumber(id, trackingNumber);
            
            if (shippedOrder == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "error", "Order not found"));
            }
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Order shipped successfully",
                    "trackingNumber", trackingNumber,
                    "data", shippedOrder
            ));
        } catch (Exception e) {
            logger.error("Error shipping order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * POST /api/orders/{id}/deliver - Vendor marks order as delivered
     * Status changes from SHIPPED to DELIVERED
     */
    @PostMapping("/{id}/deliver")
    public ResponseEntity<?> deliverOrder(@PathVariable Long id) {
        try {
            logger.info("Marking order as delivered: {}", id);
            
            Order deliveredOrder = orderService.updateOrderStatus(id, "delivered");
            
            if (deliveredOrder == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "error", "Order not found"));
            }
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Order marked as delivered",
                    "data", deliveredOrder
            ));
        } catch (Exception e) {
            logger.error("Error marking order as delivered: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
