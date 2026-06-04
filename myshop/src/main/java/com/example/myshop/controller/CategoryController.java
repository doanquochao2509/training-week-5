package com.example.myshop.controller;

import com.example.myshop.dto.category.CategoryResponse;
import com.example.myshop.dto.category.CreateCategoryRequest;
import com.example.myshop.dto.category.UpdateCategoryRequest;
import com.example.myshop.dto.common.ApiResponse;
import com.example.myshop.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<CategoryResponse> create(
            @RequestBody CreateCategoryRequest request) {

        return ApiResponse.created(
                "Tạo danh mục thành công",
                categoryService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ApiResponse<CategoryResponse> detail(
            @PathVariable UUID id) {

        return ApiResponse.success(
                "Lấy chi tiết danh mục thành công",
                categoryService.detail(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ApiResponse<Page<CategoryResponse>> search(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return ApiResponse.success(
                "Lấy danh sách danh mục thành công",
                categoryService.search(
                        keyword,
                        page,
                        size));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<CategoryResponse> update(
            @PathVariable UUID id,

            @RequestBody UpdateCategoryRequest request) {

        return ApiResponse.success(
                "Cập nhật danh mục thành công",
                categoryService.update(
                        id,
                        request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> changeStatus(

            @PathVariable UUID id,

            @RequestParam Boolean active) {

        categoryService.changeStatus(
                id,
                active);

        return ApiResponse.success(
                "Cập nhật trạng thái thành công",
                null);
    }
}