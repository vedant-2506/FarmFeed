package com.example.farmFeed.repository;

import com.example.farmFeed.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByUsername(String username);

    Optional<Admin> findByEmail(String email);

    Optional<Admin> findByUsernameAndPassword(String username, String password);

    List<Admin> findByRole(String role);

    List<Admin> findByIsActive(Boolean isActive);

    @Query("SELECT a FROM Admin a WHERE a.isActive = true ORDER BY a.createdAt DESC")
    List<Admin> getActiveAdmins();

    @Query("SELECT a FROM Admin a WHERE a.role = 'SUPER_ADMIN'")
    List<Admin> getSuperAdmins();
}
