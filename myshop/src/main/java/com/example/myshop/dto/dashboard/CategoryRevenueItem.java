package com.example.myshop.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryRevenueItem {
    private String categoryName;
    private Double amount;
    private Double percent;
}