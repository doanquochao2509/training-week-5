package com.example.myshop.service.order;

import com.example.myshop.dto.order.*;
import com.example.myshop.entity.Customer;
import com.example.myshop.entity.Order;
import com.example.myshop.entity.OrderDetail;
import com.example.myshop.entity.Product;
import com.example.myshop.exception.BadRequestException;
import com.example.myshop.exception.ResourceNotFoundException;
import com.example.myshop.repository.CustomerRepository;
import com.example.myshop.repository.OrderRepository;
import com.example.myshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    @Override
    public OrderResponse create(CreateOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Đơn hàng phải chứa ít nhất một sản phẩm");
        }
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Khách hàng không tồn tại"));
        Order order = new Order();
        order.setOrderCode(generateOrderCode());
        order.setCustomer(customer);
        order.setStatus("PENDING"); // Mặc định đơn hàng tạo mới ở trạng thái Chờ duyệt
        order.setNote(request.getNote());

        // Lấy thông tin user đang thao tác hệ thống qua Spring Security Context
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        order.setCreatedBy(currentUsername);

        double totalAmount = 0.0;
        List<OrderDetail> details = new ArrayList<>();

        for (OrderDetailRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));

            if (!product.getActive()) {
                throw new BadRequestException("Sản phẩm [" + product.getName() + "] đã ngưng kinh doanh");
            }

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new BadRequestException("Sản phẩm [" + product.getName() + "] không đủ số lượng trong kho");
            }

            // Trừ số lượng tồn kho của sản phẩm
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(product.getPrice());

            double itemAmount = product.getPrice() * item.getQuantity();
            detail.setAmount(itemAmount);

            totalAmount += itemAmount;
            details.add(detail);
        }

        order.setTotalAmount(totalAmount);
        order.setOrderDetails(details);

        orderRepository.save(order);
        return mapToResponse(order);
    }

    @Override
    public OrderResponse detail(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại"));
        return mapToResponse(order);
    }

    @Override
    public Page<OrderResponse> search(String keyword, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        return orderRepository.searchOrders(keyword, status, pageable).map(this::mapToResponse);
    }

    @Override
    public OrderResponse changeStatus(UUID id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại"));

        String currentStatus = order.getStatus();
        // Không cho phép sửa đổi đơn hàng đã hoàn thành hoặc đã huỷ trước đó
        if ("COMPLETED".equals(currentStatus) || "CANCELLED".equals(currentStatus)) {
            throw new BadRequestException("Không thể cập nhật trạng thái đơn hàng đã HOÀN THÀNH hoặc ĐÃ HUỶ");
        }

        // Nếu huỷ đơn hàng, cần phải trả lại số lượng tồn kho cho sản phẩm
        if ("CANCELLED".equalsIgnoreCase(status)) {
            for (OrderDetail detail : order.getOrderDetails()) {
                Product product = detail.getProduct();
                product.setStockQuantity(product.getStockQuantity() + detail.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(status.toUpperCase());
        orderRepository.save(order);
        return mapToResponse(order);
    }

    // Cơ chế phát sinh mã đơn hàng ngẫu nhiên (Ví dụ: DH-2026-X8B2)
    private String generateOrderCode() {
        String code;
        do {
            int randomNum = new Random().nextInt(90000) + 10000;
            code = "DH-" + randomNum;
        } while (orderRepository.existsByOrderCode(code));
        return code;
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderDetailResponse> itemResponses = order.getOrderDetails().stream()
                .map(detail -> OrderDetailResponse.builder()
                        .id(detail.getId())
                        .productId(detail.getProduct().getId())
                        .productCode(detail.getProduct().getCode())
                        .productName(detail.getProduct().getName())
                        .quantity(detail.getQuantity())
                        .unitPrice(detail.getUnitPrice())
                        .amount(detail.getAmount())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .customerName(order.getCustomer() != null ? order.getCustomer().getCustomerName() : "N/A")
                .customerPhone(order.getCustomer() != null ? order.getCustomer().getPhone() : "N/A")
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .note(order.getNote())
                .createdBy(order.getCreatedBy())
                .createdDate(order.getCreatedDate())
                .items(itemResponses)
                .build();
    }
}