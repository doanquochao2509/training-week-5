package com.example.myshop.repository;

import com.example.myshop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
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
}