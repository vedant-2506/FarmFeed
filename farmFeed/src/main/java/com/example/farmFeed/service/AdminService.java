package com.example.farmFeed.service;

import com.example.farmFeed.entity.Admin;
import com.example.farmFeed.entity.Farmer;
import com.example.farmFeed.entity.Vendor;
import com.example.farmFeed.entity.Notification;
import com.example.farmFeed.repository.AdminRepository;
import com.example.farmFeed.repository.FarmerRepository;
import com.example.farmFeed.repository.VendorRepository;
import com.example.farmFeed.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private OrderService orderService;

    /**
     * Register new admin
     */
    @Transactional
    public Admin registerAdmin(Admin admin) {
        logger.info("Registering new admin: {}", admin.getUsername());
        return adminRepository.save(admin);
    }

    /**
     * Admin login
     */
    @Transactional(readOnly = true)
    public Optional<Admin> login(String username, String password) {
        logger.info("Admin login attempt for username: {}", username);
        return adminRepository.findByUsernameAndPassword(username, password);
    }

    /**
     * Get admin by ID
     */
    @Transactional(readOnly = true)
    public Optional<Admin> getAdminById(Long id) {
        logger.info("Fetching admin: {}", id);
        return adminRepository.findById(id);
    }

    /**
     * Update admin last login
     */
    @Transactional
    public void updateLastLogin(Long adminId) {
        logger.info("Updating last login for admin: {}", adminId);
        
        Optional<Admin> admin = adminRepository.findById(adminId);
        if (admin.isPresent()) {
            Admin a = admin.get();
            a.setLastLogin(LocalDateTime.now());
            adminRepository.save(a);
        }
    }

    /**
     * Get all farmers (for admin panel)
     */
    @Transactional(readOnly = true)
    public List<Farmer> getAllFarmers() {
        logger.info("Fetching all farmers");
        return farmerRepository.findAll();
    }

    /**
     * Get all vendors (for admin panel)
     */
    @Transactional(readOnly = true)
    public List<Vendor> getAllVendors() {
        logger.info("Fetching all vendors");
        return vendorRepository.findAll();
    }

    /**
     * Approve vendor registration
     */
    @Transactional
    public Vendor approveVendor(Long vendorId) {
        logger.info("Approving vendor: {}", vendorId);
        
        Optional<Vendor> vendor = vendorRepository.findById(vendorId);
        if (vendor.isPresent()) {
            Vendor v = vendor.get();
            v.setIsApproved(true);
            vendorRepository.save(v);
            
            // Create notification
            createNotification(null, "VENDOR_APPROVED", "Vendor Approved", 
                    "New vendor " + v.getShopName() + " has been approved.", 
                    null, null, vendorId);
            
            return v;
        }
        
        return null;
    }

    /**
     * Reject vendor registration
     */
    @Transactional
    public Vendor rejectVendor(Long vendorId) {
        logger.info("Rejecting vendor: {}", vendorId);
        vendorRepository.deleteById(vendorId);
        return null;
    }

    /**
     * Suspend user (farmer or vendor)
     */
    @Transactional
    public void suspendUser(String userType, Long userId) {
        logger.info("Suspending {} with ID: {}", userType, userId);
        
        if ("farmer".equalsIgnoreCase(userType)) {
            Optional<Farmer> farmer = farmerRepository.findById(userId);
            if (farmer.isPresent()) {
                Farmer f = farmer.get();
                f.setIsActive(false);
                farmerRepository.save(f);
            }
        } else if ("vendor".equalsIgnoreCase(userType)) {
            Optional<Vendor> vendor = vendorRepository.findById(userId);
            if (vendor.isPresent()) {
                Vendor v = vendor.get();
                v.setIsActive(false);
                vendorRepository.save(v);
            }
        }
    }

    /**
     * Get system statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getSystemStats() {
        logger.info("Fetching system statistics");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalFarmers", farmerRepository.count());
        stats.put("totalVendors", vendorRepository.count());
        stats.put("activeFarmers", (long) farmerRepository.findByIsActive(true).size());
        stats.put("activeVendors", (long) vendorRepository.findByIsActive(true).size());
        stats.put("totalPendingOrders", orderService.getAllUndeliveredOrders().size());
        
        return stats;
    }

    /**
     * Create notification
     */
    @Transactional
    public Notification createNotification(Long adminId, String type, String title, String message, 
                                          Long orderId, Long productId, Long vendorId) {
        logger.info("Creating notification of type: {}", type);
        
        Notification notification = Notification.builder()
                .adminId(adminId)
                .type(type)
                .title(title)
                .message(message)
                .relatedOrderId(orderId)
                .relatedProductId(productId)
                .relatedVendorId(vendorId)
                .isRead(false)
                .build();
        
        return notificationRepository.save(notification);
    }

    /**
     * Get all notifications for admin
     */
    @Transactional(readOnly = true)
    public List<Notification> getAdminNotifications(Long adminId) {
        logger.info("Fetching notifications for admin: {}", adminId);
        return notificationRepository.findByAdminId(adminId);
    }

    /**
     * Get unread notifications
     */
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(Long adminId) {
        logger.info("Fetching unread notifications for admin: {}", adminId);
        return notificationRepository.getUnreadNotifications(adminId);
    }

    /**
     * Mark notification as read
     */
    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        logger.info("Marking notification as read: {}", notificationId);
        
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (notification.isPresent()) {
            Notification n = notification.get();
            n.setIsRead(true);
            notificationRepository.save(n);
        }
    }

    /**
     * Get unread notification count
     */
    @Transactional(readOnly = true)
    public Long getUnreadCount(Long adminId) {
        logger.info("Fetching unread count for admin: {}", adminId);
        return notificationRepository.getUnreadCount(adminId);
    }

    /**
     * Report issue/alert for admin
     */
    @Transactional
    public Notification reportIssue(Long adminId, String issue, String description) {
        logger.info("Reporting issue: {}", issue);
        
        return createNotification(adminId, "SYSTEM_ISSUE", issue, description, null, null, null);
    }

    /**
     * Get all super admins
     */
    @Transactional(readOnly = true)
    public List<Admin> getSuperAdmins() {
        logger.info("Fetching all super admins");
        return adminRepository.getSuperAdmins();
    }

    /**
     * Get vendor's inventory statistics (count and total value)
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getVendorInventoryStats(Long vendorId) {
        try {
            Vendor vendor = vendorRepository.findById(vendorId).orElse(null);
            if (vendor == null) {
                return Map.of("count", 0, "value", 0L);
            }

            // Get count and value of inventory
            Object count = vendorRepository.getVendorInventoryCounts(vendorId);
            Object value = vendorRepository.getVendorInventoryValue(vendorId);

            return Map.of(
                "count", count != null ? count : 0,
                "value", value != null ? value : 0L
            );
        } catch (Exception e) {
            logger.error("Error getting vendor inventory stats: {}", e.getMessage());
            return Map.of("count", 0, "value", 0L);
        }
    }

    /**
     * Get vendor's complete inventory details with product information
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getVendorInventoryDetails(Long vendorId) {
        try {
            Vendor vendor = vendorRepository.findById(vendorId).orElse(null);
            if (vendor == null) {
                throw new RuntimeException("Vendor not found");
            }

            // Get all inventory items for this vendor
            List<Map<String, Object>> inventoryItems = vendorRepository.getVendorInventoryDetails(vendorId);

            return Map.of(
                "items", inventoryItems,
                "count", inventoryItems.size()
            );
        } catch (Exception e) {
            logger.error("Error getting vendor inventory details: {}", e.getMessage());
            throw new RuntimeException("Error fetching inventory details: " + e.getMessage());
        }
    }

    /**
     * Get total number of admins in the system
     */
    @Transactional(readOnly = true)
    public long getTotalAdmins() {
        long count = adminRepository.count();
        logger.info("Total admins in system: {}", count);
        return count;
    }

    @Transactional
    public Admin createAdminByAdmin(Admin newAdmin, Long createdById) {
        if (adminRepository.findByUsername(newAdmin.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + newAdmin.getUsername());
        }

        if (adminRepository.findByEmail(newAdmin.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + newAdmin.getEmail());
        }

        // Set metadata
        newAdmin.setCreatedAt(LocalDateTime.now());
        newAdmin.setUpdatedAt(LocalDateTime.now());
        newAdmin.setIsActive(true);
        newAdmin.setRole("ADMIN");

        logger.info("Creating new admin {} created by admin {}", newAdmin.getUsername(), createdById);
        return adminRepository.save(newAdmin);
    }
}

