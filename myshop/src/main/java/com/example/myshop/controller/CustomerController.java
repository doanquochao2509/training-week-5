package com.example.myshop.controller;

import com.example.myshop.dto.customer.CustomerResponse;
import com.example.myshop.dto.customer.CreateCustomerRequest;
import com.example.myshop.dto.customer.UpdateCustomerRequest;
import com.example.myshop.dto.common.ApiResponse;
import com.example.myshop.service.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ApiResponse<CustomerResponse> create(
            @RequestBody CreateCustomerRequest request) {

        return ApiResponse.created(
                "Tạo khách hàng thành công",
                customerService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ApiResponse<CustomerResponse> detail(
            @PathVariable UUID id) {

        return ApiResponse.success(
                "Lấy chi tiết khách hàng thành công",
                customerService.detail(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ApiResponse<Page<CustomerResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.success(
                "Lấy danh sách khách hàng thành công",
                customerService.search(keyword, page, size));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')") // STAFF được cập nhật lại thông tin cá nhân khách
    public ApiResponse<CustomerResponse> update(
            @PathVariable UUID id,
            @RequestBody UpdateCustomerRequest request) {

        return ApiResponse.success(
                "Cập nhật khách hàng thành công",
                customerService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('MANAGER')") // Chỉ MANAGER mới được phép Khóa/Mở trạng thái hoạt động của khách
    public ApiResponse<Void> changeStatus(
            @PathVariable UUID id,
            @RequestParam Boolean active) {

        customerService.changeStatus(id, active);

        return ApiResponse.success(
                "Cập nhật trạng thái thành công",
                null);
    }
}