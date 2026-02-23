package com.example.farmFeed.repository;

import com.example.farmFeed.entity.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ============================================================
 * FarmerRepository — Spring Data JPA Repository for Farmer
 * ============================================================
 *
 * Spring Data JPA automatically generates SQL for these methods
 * based on the method name + the Farmer entity field names.
 *
 * findByPhone()    → SELECT * FROM farmer WHERE contact_number = ?
 * existsByPhone()  → SELECT COUNT(*) FROM farmer WHERE contact_number = ?
 *
 * These work because the Farmer entity has a field named "phone"
 * mapped to the "contact_number" column via @Column(name="contact_number").
 *
 * Used by:
 *   - AuthService.registerFarmer()  → existsByPhone() (duplicate check)
 *   - AuthService.authenticateFarmer() → findByPhone() (login)
 *   - AuthService.changePassword()  → findById()
 *   - FarmerService.save()          → save() (inherited from JpaRepository)
 */
@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Long> {

    Optional<Farmer> findByPhone(String phone);

    boolean existsByPhone(String phone);
}