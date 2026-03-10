package com.example.farmFeed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.farmFeed.entity.Farmer;
import java.util.Optional;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Long> {
    Optional<Farmer> findByPhone(String phone);
    Optional<Farmer> findByPhoneAndPassword(String phone, String password);
}
