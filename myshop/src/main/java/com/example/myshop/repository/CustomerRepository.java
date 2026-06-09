package com.example.myshop.repository;

import com.example.myshop.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    boolean existsByCustomerCode(String customerCode);

    @Query("SELECT c FROM Customer c WHERE " +
            "(:keyword IS NULL OR LOWER(c.customerCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.customerName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR c.phone LIKE CONCAT('%', :keyword, '%'))")
    Page<Customer> search(@Param("keyword") String keyword, Pageable pageable);
    Long countByCreatedDateBetween(LocalDateTime start, LocalDateTime end);
}