package com.example.myshop.repository;

import com.example.myshop.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository
        extends JpaRepository<Category, UUID> {

    boolean existsByCode(String code);

    Optional<Category> findById(UUID id);

    @Query("""
            SELECT c
            FROM Category c
            WHERE
                (
                    :keyword IS NULL
                    OR LOWER(c.code)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(c.name)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            """)
    Page<Category> search(
            @Param("keyword")
            String keyword,
            Pageable pageable);

    @Modifying
    @Query(value = """
            UPDATE categories
            SET is_active = :active
            WHERE id = :id
            """, nativeQuery = true)
    void updateStatus(
            @Param("id")
            UUID id,
            @Param("active")
            Boolean active);
}