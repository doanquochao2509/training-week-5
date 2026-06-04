package com.example.myshop.service.category;

import com.example.myshop.dto.category.*;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface CategoryService {

    CategoryResponse create(
            CreateCategoryRequest request);

    CategoryResponse update(
            UUID id,
            UpdateCategoryRequest request);

    void changeStatus(
            UUID id,
            Boolean active);

    CategoryResponse detail(
            UUID id);

    Page<CategoryResponse> search(
            String keyword,
            int page,
            int size);
}