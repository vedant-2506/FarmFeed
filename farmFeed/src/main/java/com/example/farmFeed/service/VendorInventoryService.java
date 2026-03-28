package com.example.farmFeed.service;

import com.example.farmFeed.entity.VendorInventory;
import com.example.farmFeed.repository.VendorInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class VendorInventoryService {

    private static final Logger logger = LoggerFactory.getLogger(VendorInventoryService.class);

    @Autowired
    private VendorInventoryRepository repository;

    @Autowired
    private FertilizerService fertilizerService;

    public VendorInventory addToInventory(Long vendorId, Integer fertilizerId, Double vendorPrice, Integer quantity) {
        try {
            Optional<VendorInventory> existing = repository.findByVendorIdAndFertilizerId(vendorId, fertilizerId);

            if (existing.isPresent()) {
                VendorInventory item = existing.get();
                item.setVendorPrice(vendorPrice);
                item.setQuantityInStock(item.getQuantityInStock() + quantity);
                item.setIsActive(true);
                return repository.save(item);
            }

            VendorInventory newItem = VendorInventory.builder()
                    .vendorId(vendorId)
                    .fertilizerId(fertilizerId)
                    .vendorPrice(vendorPrice)
                    .quantityInStock(quantity)
                    .isActive(true)
                    .build();

            return repository.save(newItem);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add to inventory: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getVendorInventory(Long vendorId) {
        try {
            List<VendorInventory> items = repository.findByVendorIdAndIsActiveTrue(vendorId);
            
            if (items.isEmpty()) {
                return new ArrayList<>();
            }

            List<Integer> fertIds = items.stream()
                    .map(VendorInventory::getFertilizerId)
                    .distinct()
                    .toList();

            List<Map<String, Object>> allFerts = fertilizerService.getFertilizersByIds(fertIds);
            
            Map<Integer, Map<String, Object>> fertMap = new HashMap<>();
            for (Map<String, Object> fert : allFerts) {
                Object fertIdObj = fert.get("fertilizer_id");
                Integer id = null;
                
                if (fertIdObj instanceof String) {
                    String fertIdStr = (String) fertIdObj;
                    if (fertIdStr.contains("_")) {
                        id = Integer.parseInt(fertIdStr.split("_")[1]);
                    } else {
                        id = Integer.parseInt(fertIdStr);
                    }
                } else if (fertIdObj instanceof Number) {
                    id = ((Number) fertIdObj).intValue();
                }
                
                if (id != null) {
                    fertMap.put(id, fert);
                }
            }

            List<Map<String, Object>> result = new ArrayList<>();
            for (VendorInventory item : items) {
                Map<String, Object> data = fertMap.get(item.getFertilizerId());
                
                if (data != null) {
                    Map<String, Object> inv = new HashMap<>(data);
                    inv.put("inventory_id", item.getId());
                    inv.put("vendor_price", item.getVendorPrice());
                    inv.put("quantity_in_stock", item.getQuantityInStock());
                    inv.put("base_price", data.get("price"));
                    inv.put("added_at", item.getAddedAt());
                    result.add(inv);
                }
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch inventory: " + e.getMessage());
        }
    }

    public VendorInventory updateInventoryItem(Long vendorId, Integer fertilizerId, Double newPrice, Integer newQuantity) {
        try {
            Optional<VendorInventory> item = repository.findByVendorIdAndFertilizerId(vendorId, fertilizerId);

            if (item.isEmpty()) {
                throw new RuntimeException("Inventory item not found");
            }

            VendorInventory updated = item.get();
            if (newPrice != null) updated.setVendorPrice(newPrice);
            if (newQuantity != null) updated.setQuantityInStock(newQuantity);

            return repository.save(updated);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update inventory: " + e.getMessage());
        }
    }

    public boolean removeFromInventory(Long vendorId, Integer fertilizerId) {
        try {
            Optional<VendorInventory> item = repository.findByVendorIdAndFertilizerId(vendorId, fertilizerId);

            if (item.isEmpty()) {
                throw new RuntimeException("Inventory item not found");
            }

            VendorInventory removed = item.get();
            removed.setIsActive(false);
            repository.save(removed);

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove from inventory: " + e.getMessage());
        }
    }

    /**
     * Check if vendor already has a fertilizer
     */
    public boolean hasInventoryItem(Long vendorId, Integer fertilizerId) {
        return repository.existsByVendorIdAndFertilizerId(vendorId, fertilizerId);
    }

    /**
     * Get total inventory count for vendor
     */
    public long getInventoryCount(Long vendorId) {
        return repository.findByVendorIdAndIsActiveTrue(vendorId).size();
    }
}
