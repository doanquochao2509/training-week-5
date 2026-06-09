package com.example.myshop.controller;

import com.example.myshop.dto.common.ApiResponse;
import com.example.myshop.dto.product.CreateProductRequest;
import com.example.myshop.dto.product.ProductResponse;
import com.example.myshop.dto.product.UpdateProductRequest;
import com.example.myshop.exception.BadRequestException;
import com.example.myshop.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<ProductResponse> create(
            @RequestBody CreateProductRequest request) {

        return ApiResponse.created(
                "Tạo sản phẩm thành công",
                productService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ApiResponse<ProductResponse> detail(
            @PathVariable UUID id) {

        return ApiResponse.success(
                "Lấy chi tiết sản phẩm thành công",
                productService.detail(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ApiResponse<Page<ProductResponse>> search(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return ApiResponse.success(
                "Lấy danh sách sản phẩm thành công",
                productService.search(
                        keyword,
                        page,
                        size));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<ProductResponse> update(
            @PathVariable UUID id,

            @RequestBody UpdateProductRequest request) {

        return ApiResponse.success(
                "Cập nhật sản phẩm thành công",
                productService.update(
                        id,
                        request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> changeStatus(

            @PathVariable UUID id,

            @RequestParam Boolean active) {

        productService.changeStatus(
                id,
                active);

        return ApiResponse.success(
                "Cập nhật trạng thái thành công",
                null);
    }

    @PostMapping("/{id}/image")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<ProductResponse> uploadImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {

        // Validate
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Chỉ chấp nhận file ảnh");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BadRequestException("File không được vượt quá 5MB");
        }

        return ApiResponse.success(
                "Upload ảnh thành công",
                productService.uploadImage(id, file));
    }

    @DeleteMapping("/{id}/image")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> deleteImage(@PathVariable UUID id) {
        productService.deleteImage(id);
        return ApiResponse.success("Xóa ảnh thành công", null);
    }
}