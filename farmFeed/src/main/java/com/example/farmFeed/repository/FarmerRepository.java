package com.example.farmFeed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.farmFeed.entity.Farmer;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Long> {
}
