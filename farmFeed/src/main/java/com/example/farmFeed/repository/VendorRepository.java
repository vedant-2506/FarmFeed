package com.example.farmFeed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.farmFeed.entity.Vendor;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByEmail(String email);
    Optional<Vendor> findByEmailAndPassword(String email, String password);
    Optional<Vendor> findByLicenseNumber(String licenseNumber);
}
