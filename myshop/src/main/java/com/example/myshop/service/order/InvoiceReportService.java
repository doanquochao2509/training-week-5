package com.example.myshop.service.order;

import com.example.myshop.dto.order.OrderDetailResponse;
import com.example.myshop.dto.order.OrderResponse;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InvoiceReportService {

    public byte[] exportInvoicePdf(OrderResponse order) throws Exception {

        // Load và compile template
        InputStream jrxmlStream = new ClassPathResource("reports/invoice.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);

        // Parameters truyền vào
        Map<String, Object> params = new HashMap<>();
        params.put("orderCode",     order.getOrderCode());
        params.put("customerName",  order.getCustomerName());
        params.put("customerPhone", order.getCustomerPhone() != null ? order.getCustomerPhone() : "---");
        params.put("status",        translateStatus(order.getStatus()));
        params.put("note",          order.getNote() != null && !order.getNote().isBlank() ? order.getNote() : "---");
        params.put("createdBy",     order.getCreatedBy());
        params.put("createdDate", order.getCreatedDate() != null
                ? order.getCreatedDate().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "---");
        params.put("totalAmount",   order.getTotalAmount());
        params.put("discountPercent", order.getDiscountPercent() != null ? order.getDiscountPercent() : 0.0);
        params.put("discountAmount",  order.getDiscountAmount() != null ? order.getDiscountAmount() : 0.0);

        // Data source từ items
        List<Map<String, ?>> rows = new ArrayList<>();
        for (OrderDetailResponse item : order.getItems()) {
            Map<String, Object> row = new HashMap<>();
            row.put("productCode", item.getProductCode());
            row.put("productName", item.getProductName());
            row.put("quantity",    item.getQuantity());
            row.put("unitPrice",   item.getUnitPrice());
            row.put("amount",      item.getAmount());
            rows.add(row);
        }

        JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(rows);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private String translateStatus(String status) {
        return switch (status) {
            case "PENDING"   -> "Chờ xử lý";
            case "CONFIRMED" -> "Đã xác nhận";
            case "COMPLETED" -> "Hoàn thành";
            case "CANCELLED" -> "Đã hủy";
            default          -> status;
        };
    }
}