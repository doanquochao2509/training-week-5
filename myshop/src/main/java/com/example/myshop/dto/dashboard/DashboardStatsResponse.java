package com.example.myshop.dto.dashboard;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DashboardStatsResponse {
    private Long totalOrders;
    private Double revenueTotal;
    private Long lowStockProducts;
    private Long newCustomers;
    private List<Double> chartRevenue;        // 7 cột theo period
    private List<String> chartLabels;         // nhãn trục X
    private List<CategoryRevenueItem> categoryRevenue;
    private List<ProductRevenueItem> topProducts; // ✅ top 5 sản phẩm
    private String period;                    // day | week | month
}