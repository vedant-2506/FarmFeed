package com.example.farmFeed.controller;

import com.example.farmFeed.entity.Admin;
import com.example.farmFeed.entity.Farmer;
import com.example.farmFeed.entity.Vendor;
import com.example.farmFeed.entity.Notification;
import com.example.farmFeed.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody Admin admin) {
        try {
            long count = adminService.getTotalAdmins();
            if (count > 0) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "error", "Only first admin can register"));
            }
            
            Admin registered = adminService.registerAdmin(admin);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Admin registered successfully",
                    "adminId", registered.getId(),
                    "username", registered.getUsername(),
                    "fullName", registered.getFullName()
            ));
        } catch (Exception e) {
            logger.error("Error registering admin: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * POST /api/admin/{adminId}/create-admin - Create new admin (REQUIRES AUTHENTICATION)
     * Only existing admins can create other admins
     */
    @PostMapping("/{adminId}/create-admin")
    public ResponseEntity<?> createNewAdmin(
            @PathVariable Long adminId,
            @RequestBody Admin newAdmin) {
        try {
            // Verify that the requester is a valid admin
            Optional<Admin> requesterOpt = adminService.getAdminById(adminId);
            if (!requesterOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "error", "Admin not found or not authenticated"));
            }
            
            Admin requester = requesterOpt.get();
            if (!requester.getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "error", "Admin account is inactive"));
            }
            
            logger.info("Admin {} creating new admin: {}", adminId, newAdmin.getUsername());
            Admin createdAdmin = adminService.createAdminByAdmin(newAdmin, adminId);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "New admin created successfully",
                    "adminId", createdAdmin.getId(),
                    "username", createdAdmin.getUsername(),
                    "fullName", createdAdmin.getFullName()
            ));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating admin: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * POST /api/admin/login - Admin login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            logger.info("Admin login attempt");
            
            String username = credentials.get("username");
            String password = credentials.get("password");
            
            Optional<Admin> admin = adminService.login(username, password);
            
            if (admin.isPresent()) {
                adminService.updateLastLogin(admin.get().getId());
                
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Login successful",
                        "adminId", admin.get().getId(),
                        "username", admin.get().getUsername(),
                        "role", admin.get().getRole()
                ));
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "error", "Invalid credentials"));
        } catch (Exception e) {
            logger.error("Error during admin login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/{id} - Get admin details
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAdmin(@PathVariable Long id) {
        try {
            logger.info("Fetching admin: {}", id);
            Optional<Admin> admin = adminService.getAdminById(id);
            
            if (admin.isPresent()) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "data", admin.get()
                ));
            }
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "error", "Admin not found"));
        } catch (Exception e) {
            logger.error("Error fetching admin: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/users/farmers - Get all farmers
     */
    @GetMapping("/users/farmers")
    public ResponseEntity<?> getAllFarmers() {
        try {
            logger.info("Fetching all farmers");
            List<Farmer> farmers = adminService.getAllFarmers();
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", farmers.size(),
                    "data", farmers
            ));
        } catch (Exception e) {
            logger.error("Error fetching farmers: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/users/vendors - Get all vendors
     */
    @GetMapping("/users/vendors")
    public ResponseEntity<?> getAllVendors() {
        try {
            logger.info("Fetching all vendors");
            List<Vendor> vendors = adminService.getAllVendors();
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", vendors.size(),
                    "data", vendors
            ));
        } catch (Exception e) {
            logger.error("Error fetching vendors: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/vendor/{vendorId}/approve - Approve vendor
     */
    @PutMapping("/vendor/{vendorId}/approve")
    public ResponseEntity<?> approveVendor(@PathVariable Long vendorId) {
        try {
            logger.info("Approving vendor: {}", vendorId);
            
            Vendor vendor = adminService.approveVendor(vendorId);
            
            if (vendor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "error", "Vendor not found"));
            }
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Vendor approved successfully",
                    "data", vendor
            ));
        } catch (Exception e) {
            logger.error("Error approving vendor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/admin/vendor/{vendorId}/reject - Reject vendor
     */
    @DeleteMapping("/vendor/{vendorId}/reject")
    public ResponseEntity<?> rejectVendor(@PathVariable Long vendorId) {
        try {
            logger.info("Rejecting vendor: {}", vendorId);
            
            adminService.rejectVendor(vendorId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Vendor rejected successfully"
            ));
        } catch (Exception e) {
            logger.error("Error rejecting vendor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/users/suspend - Suspend user
     */
    @PutMapping("/users/suspend")
    public ResponseEntity<?> suspendUser(@RequestBody Map<String, Object> request) {
        try {
            logger.info("Suspending user");
            
            String userType = (String) request.get("userType");
            Long userId = ((Number) request.get("userId")).longValue();
            
            adminService.suspendUser(userType, userId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User suspended successfully"
            ));
        } catch (Exception e) {
            logger.error("Error suspending user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/statistics - Get system statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getSystemStats() {
        try {
            logger.info("Fetching system statistics");
            Map<String, Object> stats = adminService.getSystemStats();
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", stats
            ));
        } catch (Exception e) {
            logger.error("Error fetching statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/{adminId}/notifications - Get admin notifications
     */
    @GetMapping("/{adminId}/notifications")
    public ResponseEntity<?> getNotifications(@PathVariable Long adminId) {
        try {
            logger.info("Fetching notifications for admin: {}", adminId);
            List<Notification> notifications = adminService.getAdminNotifications(adminId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", notifications.size(),
                    "data", notifications
            ));
        } catch (Exception e) {
            logger.error("Error fetching notifications: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/{adminId}/notifications/unread - Get unread notifications
     */
    @GetMapping("/{adminId}/notifications/unread")
    public ResponseEntity<?> getUnreadNotifications(@PathVariable Long adminId) {
        try {
            logger.info("Fetching unread notifications for admin: {}", adminId);
            List<Notification> notifications = adminService.getUnreadNotifications(adminId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", notifications.size(),
                    "data", notifications
            ));
        } catch (Exception e) {
            logger.error("Error fetching unread notifications: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/{adminId}/notifications/unread-count - Get unread count
     */
    @GetMapping("/{adminId}/notifications/unread-count")
    public ResponseEntity<?> getUnreadCount(@PathVariable Long adminId) {
        try {
            logger.info("Fetching unread count for admin: {}", adminId);
            Long count = adminService.getUnreadCount(adminId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "unreadCount", count
            ));
        } catch (Exception e) {
            logger.error("Error fetching unread count: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * PUT /api/admin/notifications/{notificationId}/read - Mark notification as read
     */
    @PutMapping("/notifications/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId) {
        try {
            logger.info("Marking notification as read: {}", notificationId);
            
            adminService.markNotificationAsRead(notificationId);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notification marked as read"
            ));
        } catch (Exception e) {
            logger.error("Error marking notification as read: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * POST /api/admin/report-issue - Report system issue
     */
    @PostMapping("/report-issue")
    public ResponseEntity<?> reportIssue(@RequestBody Map<String, String> request) {
        try {
            logger.info("Reporting system issue");
            
            Long adminId = Long.parseLong(request.get("adminId"));
            String issue = request.get("issue");
            String description = request.get("description");
            
            Notification notification = adminService.reportIssue(adminId, issue, description);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Issue reported successfully",
                    "notificationId", notification.getId()
            ));
        } catch (Exception e) {
            logger.error("Error reporting issue: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/vendors - Get all vendors with inventory statistics
     */
    @GetMapping("/vendors")
    public ResponseEntity<?> getAllVendorsWithStats() {
        try {
            List<Vendor> vendors = adminService.getAllVendors();
            logger.info("Fetching all vendors with stats: count = {}", vendors.size());

            List<Map<String, Object>> vendorList = new ArrayList<>();

            for (Vendor vendor : vendors) {
                Map<String, Object> vendorData = new HashMap<>();
                vendorData.put("id", vendor.getId());
                vendorData.put("shop_name", vendor.getShopName());
                vendorData.put("owner_name", vendor.getOwnerName());
                vendorData.put("email", vendor.getEmail());
                vendorData.put("phone", vendor.getPhone());
                vendorData.put("address", vendor.getShopAddress());
                vendorData.put("is_active", vendor.getIsActive());

                // Get inventory statistics from admin service
                Map<String, Object> stats = adminService.getVendorInventoryStats(vendor.getId());
                vendorData.put("inventoryCount", stats.get("count"));
                vendorData.put("inventoryValue", stats.get("value"));

                vendorList.add(vendorData);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", vendorList,
                    "count", vendorList.size()
            ));
        } catch (Exception e) {
            logger.error("Error fetching vendors: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Error fetching vendors: " + e.getMessage()
            ));
        }
    }

    /**
     * GET /api/admin/vendor/{id}/inventory - Get vendor's inventory with product details
     */
    @GetMapping("/vendor/{vendorId}/inventory")
    public ResponseEntity<?> getVendorInventory(@PathVariable Long vendorId) {
        try {
            logger.info("Fetching inventory for vendor: {}", vendorId);

            Map<String, Object> inventory = adminService.getVendorInventoryDetails(vendorId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", inventory.get("items"),
                    "count", inventory.get("count")
            ));
        } catch (Exception e) {
            logger.error("Error fetching vendor inventory: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Error fetching inventory: " + e.getMessage()
            ));
        }
    }
}
