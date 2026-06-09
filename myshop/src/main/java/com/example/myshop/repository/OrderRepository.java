package com.example.myshop.repository;

import com.example.myshop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    boolean existsByOrderCode(String orderCode);

    @Query("SELECT o FROM Order o WHERE " +
            "(:keyword IS NULL OR LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR o.status = :status)")
    Page<Order> searchOrders(@Param("keyword") String keyword,
                             @Param("status") String status,
                             Pageable pageable);
    Long countByCreatedDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.createdDate BETWEEN :start AND :end AND o.status = 'COMPLETED'")
    Double sumRevenueToday(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    @Query("""
    SELECT p.category.name, SUM(od.amount)
    FROM OrderDetail od
    JOIN od.product p
    JOIN od.order o
    WHERE o.createdDate BETWEEN :start AND :end
    AND o.status = 'COMPLETED'
    GROUP BY p.category.name
    """)
    List<Object[]> revenueByCategory(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    @Query("""
    SELECT p.name, p.code, SUM(od.quantity), SUM(od.amount)
    FROM OrderDetail od
    JOIN od.product p
    JOIN od.order o
    WHERE o.createdDate BETWEEN :start AND :end
    AND o.status = 'COMPLETED'
    GROUP BY p.name, p.code
    ORDER BY SUM(od.amount) DESC
    """)
    List<Object[]> topProductsByRevenue(
            @Param("start") LocalDateTime start,
            @Param("end")   LocalDateTime end
    );
}