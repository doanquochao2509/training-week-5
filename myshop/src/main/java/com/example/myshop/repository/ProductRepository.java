package com.example.myshop.repository;

import com.example.myshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository
        extends JpaRepository<Product, UUID> {

    boolean existsByCode(String code);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category " +
            "WHERE (:keyword IS NULL OR " +
            "LOWER(p.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> search(
            @Param("keyword") String keyword,
            Pageable pageable);
}