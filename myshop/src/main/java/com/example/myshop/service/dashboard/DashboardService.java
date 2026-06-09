package com.example.myshop.service.dashboard;

import com.example.myshop.dto.dashboard.DashboardStatsResponse;

public interface DashboardService {
    DashboardStatsResponse getStats(String period);
    byte[] exportRevenuePdf(String period) throws Exception;
}