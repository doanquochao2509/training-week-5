package com.example.myshop.dto.order;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateOrderRequest {
    private UUID customerId;
    private String note;
    private List<OrderDetailRequest> items;
}