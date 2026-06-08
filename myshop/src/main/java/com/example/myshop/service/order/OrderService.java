package com.example.myshop.service.order;

import com.example.myshop.dto.order.CreateOrderRequest;
import com.example.myshop.dto.order.OrderResponse;
import org.springframework.data.domain.Page;
import java.util.UUID;

public interface OrderService {
    OrderResponse create(CreateOrderRequest request);
    OrderResponse detail(UUID id);
    Page<OrderResponse> search(String keyword, String status, int page, int size);
    OrderResponse changeStatus(UUID id, String status);
    byte[] exportInvoicePdf(UUID id) throws Exception;
}