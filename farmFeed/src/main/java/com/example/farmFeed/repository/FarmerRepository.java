package com.example.farmFeed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.farmFeed.entity.Farmer;
import java.util.Optional;
import java.util.List;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Long> {
    Optional<Farmer> findByPhone(String phone);
    Optional<Farmer> findByEmail(String email);
    Optional<Farmer> findByPhoneAndPassword(String phone, String password);
    Optional<Farmer> findByEmailAndPassword(String email, String password);
    List<Farmer> findByIsActive(Boolean isActive);
    
    @Query("SELECT f FROM Farmer f WHERE f.isActive = true ORDER BY f.createdAt DESC")
    List<Farmer> getActiveFarmers();
}
