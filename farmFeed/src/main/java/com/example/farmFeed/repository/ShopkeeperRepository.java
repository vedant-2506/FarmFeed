package com.example.farmFeed.repository;

import com.example.farmFeed.entity.Shopkeeper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ============================================================
 * ShopkeeperRepository — Spring Data JPA Repository for Shopkeeper
 * ============================================================
 *
 * BUG FIXED:
 *   Previously named "shopkeeperRepository" (lowercase 's').
 *   Java class names MUST use PascalCase. On Linux (case-sensitive
 *   filesystem), the compiler cannot find the file if the filename
 *   and class name don't match exactly → compile error at build time.
 *   Renamed to "ShopkeeperRepository". Import in ShopkeeperService updated.
 *
 * Spring Data JPA auto-generates SQL from method names:
 *   findByPhone()      → SELECT * FROM shopkeeper WHERE contact_number = ?
 *   existsByPhone()    → SELECT COUNT(*) FROM shopkeeper WHERE contact_number = ?
 *   findByShopName()   → SELECT * FROM shopkeeper WHERE shop_name = ?
 *   existsByShopName() → SELECT COUNT(*) FROM shopkeeper WHERE shop_name = ?
 *
 * Used by:
 *   - ShopkeeperService.registerShopkeeper() → existsByPhone(), existsByShopName()
 *   - ShopkeeperService.authenticateShopkeeper() → findByPhone()
 *   - ShopkeeperService.changePassword() → findById()
 *   - ShopkeeperService.getShopkeeperById() → findById()
 */
@Repository
public interface ShopkeeperRepository extends JpaRepository<Shopkeeper, Long> {

    Optional<Shopkeeper> findByPhone(String phone);

    boolean existsByPhone(String phone);

    Optional<Shopkeeper> findByShopName(String shopName);

    boolean existsByShopName(String shopName);
}