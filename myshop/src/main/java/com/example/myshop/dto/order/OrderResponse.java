package com.example.myshop.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private UUID id;
    private String orderCode;
    private UUID customerId;
    private String customerName;
    private String customerPhone;
    private Double totalAmount;
    private String status;
    private String note;
    private String createdBy;
    private LocalDateTime createdDate;
    private List<OrderDetailResponse> items;
}