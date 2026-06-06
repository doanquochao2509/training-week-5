package com.example.myshop.dto.order;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class OrderDetailRequest {
    private UUID productId;
    private Integer quantity;
}