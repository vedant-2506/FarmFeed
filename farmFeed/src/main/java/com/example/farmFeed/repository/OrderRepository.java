package com.example.farmFeed.repository;

import com.example.farmFeed.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByFarmerId(Long farmerId);

    List<Order> findByVendorId(Long vendorId);

    List<Order> findByStatus(String status);

    @Query("SELECT o FROM Order o WHERE o.farmerId = :farmerId ORDER BY o.orderDate DESC")
    List<Order> getOrderHistoryByFarmer(@Param("farmerId") Long farmerId);

    @Query("SELECT o FROM Order o WHERE o.vendorId = :vendorId AND o.status = 'pending'")
    List<Order> getPendingOrdersByVendor(@Param("vendorId") Long vendorId);

    @Query("SELECT o FROM Order o WHERE o.vendorId = :vendorId AND o.status = 'delivered' ORDER BY o.deliveryDate DESC")
    List<Order> getDeliveredOrdersByVendor(@Param("vendorId") Long vendorId);

    @Query("SELECT o FROM Order o WHERE o.farmerId = :farmerId AND o.status = 'pending' ORDER BY o.orderDate DESC")
    List<Order> getPendingOrdersByFarmer(@Param("farmerId") Long farmerId);

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.vendorId = :vendorId AND o.status = 'delivered' AND o.orderDate >= :startDate AND o.orderDate <= :endDate")
    Double getMonthlyIncomeByVendor(@Param("vendorId") Long vendorId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.vendorId = :vendorId AND o.status = 'pending'")
    Long getPendingOrderCount(@Param("vendorId") Long vendorId);

    @Query("SELECT o FROM Order o WHERE o.status = 'pending' OR o.status = 'shipped' ORDER BY o.orderDate ASC")
    List<Order> getAllUndeliveredOrders();
}
