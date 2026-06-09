package com.example.myshop.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductRevenueItem {
    private String productName;
    private String productCode;
    private Long   totalQuantity;
    private Double totalAmount;
}