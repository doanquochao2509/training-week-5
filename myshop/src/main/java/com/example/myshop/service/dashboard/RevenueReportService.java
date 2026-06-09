package com.example.myshop.service.dashboard;

import com.example.myshop.dto.dashboard.*;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RevenueReportService {

    public byte[] exportRevenuePdf(DashboardStatsResponse stats) throws Exception {

        InputStream jrxmlStream = new ClassPathResource("reports/revenue.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);

        String periodLabel = switch (stats.getPeriod()) {
            case "day"   -> "Hôm nay";
            case "month" -> "Tháng này";
            default      -> "Tuần này";
        };

        Map<String, Object> params = new HashMap<>();
        params.put("period",       periodLabel);
        params.put("revenueTotal", stats.getRevenueTotal());
        params.put("totalOrders",  stats.getTotalOrders());
        params.put("newCustomers", stats.getNewCustomers());
        params.put("exportDate",   java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        List<Map<String, ?>> rows = new ArrayList<>();

        // Section 1: Top sản phẩm (data thật)
        for (ProductRevenueItem p : stats.getTopProducts()) {
            Map<String, Object> row = new HashMap<>();
            row.put("sectionType",  "PRODUCT");
            row.put("productCode",  p.getProductCode());
            row.put("productName",  p.getProductName());
            row.put("quantity",     p.getTotalQuantity());
            row.put("amount",       p.getTotalAmount());
            row.put("categoryName", "");
            row.put("percent",      0.0);
            row.put("chartLabel",   "");
            row.put("chartRevenue", 0.0);
            rows.add(row);
        }

        // Section 2: Header danh mục (1 row duy nhất)
        rows.add(buildHeaderRow("CATEGORY_HEADER"));

        // Section 2: Data danh mục (data thật)
        for (CategoryRevenueItem c : stats.getCategoryRevenue()) {
            Map<String, Object> row = new HashMap<>();
            row.put("sectionType",  "CATEGORY");
            row.put("productCode",  "");
            row.put("productName",  "");
            row.put("quantity",     0L);
            row.put("amount",       c.getAmount());
            row.put("categoryName", c.getCategoryName());
            row.put("percent",      c.getPercent());
            row.put("chartLabel",   "");
            row.put("chartRevenue", 0.0);
            rows.add(row);
        }

        // Section 3: Header doanh thu theo kỳ (1 row duy nhất)
        rows.add(buildHeaderRow("CHART_HEADER"));

        // Section 3: Data chart (data thật)
        List<String> labels   = stats.getChartLabels();
        List<Double> revenues = stats.getChartRevenue();
        for (int i = 0; i < labels.size(); i++) {
            Map<String, Object> row = new HashMap<>();
            row.put("sectionType",  "CHART");
            row.put("productCode",  "");
            row.put("productName",  "");
            row.put("quantity",     0L);
            row.put("amount",       0.0);
            row.put("categoryName", "");
            row.put("percent",      0.0);
            row.put("chartLabel",   labels.get(i));
            row.put("chartRevenue", revenues.get(i));
            rows.add(row);
        }

        JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(rows);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    // Helper tạo row header
    private Map<String, Object> buildHeaderRow(String sectionType) {
        Map<String, Object> row = new HashMap<>();
        row.put("sectionType",  sectionType);
        row.put("productCode",  "");
        row.put("productName",  "");
        row.put("quantity",     0L);
        row.put("amount",       0.0);
        row.put("categoryName", "");
        row.put("percent",      0.0);
        row.put("chartLabel",   "");
        row.put("chartRevenue", 0.0);
        return row;
    }
}