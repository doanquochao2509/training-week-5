package com.example.myshop.controller;

import com.example.myshop.dto.common.ApiResponse;
import com.example.myshop.dto.dashboard.DashboardStatsResponse;
import com.example.myshop.service.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ApiResponse<DashboardStatsResponse> getStats(
            @RequestParam(defaultValue = "week") String period) {
        return ApiResponse.success(
                "Lấy thống kê thành công",
                dashboardService.getStats(period));
    }

    @GetMapping("/export-pdf")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(defaultValue = "week") String period) throws Exception {
        byte[] pdf = dashboardService.exportRevenuePdf(period);
        String filename = "revenue-report-" + period + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}