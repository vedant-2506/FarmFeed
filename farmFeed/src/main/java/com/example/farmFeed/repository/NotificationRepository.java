package com.example.farmFeed.repository;

import com.example.farmFeed.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByAdminId(Long adminId);

    @Query("SELECT n FROM Notification n WHERE n.adminId = :adminId AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> getUnreadNotifications(@Param("adminId") Long adminId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.adminId = :adminId AND n.isRead = false")
    Long getUnreadCount(@Param("adminId") Long adminId);

    @Query("SELECT n FROM Notification n WHERE n.adminId = :adminId ORDER BY n.createdAt DESC LIMIT :limit")
    List<Notification> getLatestNotifications(@Param("adminId") Long adminId, @Param("limit") Integer limit);

    @Query("SELECT n FROM Notification n WHERE n.type = :type AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> getNotificationsByType(@Param("type") String type);
}
