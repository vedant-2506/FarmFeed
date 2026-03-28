package com.example.farmFeed.controller;

import com.example.farmFeed.service.VendorInventoryService;
import com.example.farmFeed.service.FertilizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/vendor-inventory")
public class VendorInventoryController {

    private static final Logger logger = LoggerFactory.getLogger(VendorInventoryController.class);

    @Autowired
    private VendorInventoryService inventoryService;

    @Autowired
    private FertilizerService fertilizerService;

    private Integer parseFertilizerId(String id) {
        if (id.contains("_")) {
            return Integer.parseInt(id.split("_")[1]);
        }
        return Integer.parseInt(id);
    }

    @GetMapping("/{vendorId}")
    public ResponseEntity<?> getVendorInventory(@PathVariable Long vendorId) {
        try {
            List<Map<String, Object>> inventory = inventoryService.getVendorInventory(vendorId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", inventory.size(),
                    "data", inventory
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToInventory(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("vendorId") == null) {
                throw new IllegalArgumentException("vendorId required");
            }
            if (request.get("fertilizerId") == null) {
                throw new IllegalArgumentException("fertilizerId required");
            }
            if (request.get("vendorPrice") == null) {
                throw new IllegalArgumentException("vendorPrice required");
            }
            if (request.get("quantity") == null) {
                throw new IllegalArgumentException("quantity required");
            }

            Long vendorId = Long.parseLong(request.get("vendorId").toString());
            String fertId = request.get("fertilizerId").toString();
            
            if ("undefined".equals(fertId) || fertId.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid fertilizer ID");
            }
            
            Integer fertilizerId = parseFertilizerId(fertId);
            Double price = Double.parseDouble(request.get("vendorPrice").toString());
            Integer qty = Integer.parseInt(request.get("quantity").toString());

            var result = inventoryService.addToInventory(vendorId, fertilizerId, price, qty);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Added to inventory successfully",
                    "data", result
            ));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request to add inventory: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", "Invalid input: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error adding to inventory: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * POST /api/vendor-inventory/check - Check if vendor already has fertilizer
     */
    @PostMapping("/check")
    public ResponseEntity<?> checkInventoryItem(@RequestBody Map<String, Object> request) {
        try {
            Long vendorId = Long.parseLong(request.get("vendorId").toString());
            String fertilizerIdStr = request.get("fertilizerId").toString();
            Integer fertilizerId = parseFertilizerId(fertilizerIdStr);

            boolean exists = inventoryService.hasInventoryItem(vendorId, fertilizerId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "exists", exists
            ));
        } catch (Exception e) {
            logger.error("Error checking inventory: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * PUT /api/vendor-inventory/update - Update price and quantity
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateInventoryItem(@RequestBody Map<String, Object> request) {
        try {
            Long vendorId = Long.parseLong(request.get("vendorId").toString());
            String fertilizerIdStr = request.get("fertilizerId").toString();
            Integer fertilizerId = parseFertilizerId(fertilizerIdStr);
            
            Double vendorPrice = request.get("vendorPrice") != null ?
                    Double.parseDouble(request.get("vendorPrice").toString()) : null;
            Integer quantity = request.get("quantity") != null ?
                    Integer.parseInt(request.get("quantity").toString()) : null;

            logger.info("Updating inventory for vendor {} - fertilizer {} (str: {})", vendorId, fertilizerId, fertilizerIdStr);

            var result = inventoryService.updateInventoryItem(vendorId, fertilizerId, vendorPrice, quantity);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Updated successfully",
                    "data", result
            ));
        } catch (Exception e) {
            logger.error("Error updating inventory: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/vendor-inventory/{vendorId}/{fertilizerId} - Remove from inventory
     */
    @DeleteMapping("/{vendorId}/{fertilizerId}")
    public ResponseEntity<?> removeFromInventory(
            @PathVariable Long vendorId,
            @PathVariable Integer fertilizerId) {
        try {
            logger.info("Removing fertilizer {} from vendor {} inventory", fertilizerId, vendorId);

            boolean success = inventoryService.removeFromInventory(vendorId, fertilizerId);

            if (success) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Removed from inventory successfully"
                ));
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "error", "Inventory item not found"));
        } catch (Exception e) {
            logger.error("Error removing from inventory: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * GET /api/vendor-inventory/{vendorId}/count - Get inventory count
     */
    @GetMapping("/{vendorId}/count")
    public ResponseEntity<?> getInventoryCount(@PathVariable Long vendorId) {
        try {
            long count = inventoryService.getInventoryCount(vendorId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", count
            ));
        } catch (Exception e) {
            logger.error("Error getting inventory count: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
