package com.example.myshop.controller;

import com.example.myshop.dto.common.ApiResponse;
import com.example.myshop.dto.order.CreateOrderRequest;
import com.example.myshop.dto.order.OrderResponse;
import com.example.myshop.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')") // Cả nhân viên và quản lý đều được lên đơn
    public ApiResponse<OrderResponse> create(@RequestBody CreateOrderRequest request) {
        return ApiResponse.created(
                "Khởi tạo đơn hàng thành công",
                orderService.create(request)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ApiResponse<OrderResponse> detail(@PathVariable UUID id) {
        return ApiResponse.success(
                "Lấy chi tiết thông tin đơn hàng thành công",
                orderService.detail(id)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ApiResponse<Page<OrderResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.success(
                "Lấy danh sách quản lý đơn hàng thành công",
                orderService.search(keyword, status, page, size)
        );
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('MANAGER')") // Chỉ có Quản lý mới có quyền duyệt đơn hoặc thay đổi trạng thái
    public ApiResponse<OrderResponse> changeStatus(
            @PathVariable UUID id,
            @RequestParam String status) {

        return ApiResponse.success(
                "Cập nhật trạng thái xử lý đơn hàng thành công",
                orderService.changeStatus(id, status)
        );
    }
}