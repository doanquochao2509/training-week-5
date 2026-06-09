package com.example.myshop.service.dashboard;

import com.example.myshop.dto.dashboard.*;
import com.example.myshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository    orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository  productRepository;
    private final RevenueReportService revenueReportService;

    @Override
    public DashboardStatsResponse getStats(String period) {
        LocalDateTime[] range = getRange(period);
        LocalDateTime start = range[0], end = range[1];

        Long   totalOrders     = orderRepository.countByCreatedDateBetween(start, end);
        Double revenueTotal    = orderRepository.sumRevenueToday(start, end);
        Long   lowStock        = productRepository.countByStockQuantityLessThanEqualAndActiveTrue(10L);
        Long   newCustomers    = customerRepository.countByCreatedDateBetween(start, end);

        // Chart bars + labels
        List<Double> chartRevenue = new ArrayList<>();
        List<String> chartLabels  = new ArrayList<>();
        buildChart(period, chartRevenue, chartLabels);

        // Category revenue
        List<Object[]> rawCat = orderRepository.revenueByCategory(start, end);
        double totalWeek = rawCat.stream()
                .mapToDouble(r -> r[1] != null ? ((Number) r[1]).doubleValue() : 0.0)
                .sum();
        List<CategoryRevenueItem> categoryRevenue = rawCat.stream()
                .map(r -> {
                    String name   = (String) r[0];
                    double amount = r[1] != null ? ((Number) r[1]).doubleValue() : 0.0;
                    double pct    = totalWeek > 0 ? Math.round(amount / totalWeek * 1000.0) / 10.0 : 0.0;
                    return new CategoryRevenueItem(name, amount, pct);
                })
                .sorted((a, b) -> Double.compare(b.getAmount(), a.getAmount()))
                .limit(5)
                .collect(Collectors.toList());

        // Top 5 products
        List<Object[]> rawProd = orderRepository.topProductsByRevenue(start, end);
        List<ProductRevenueItem> topProducts = rawProd.stream()
                .limit(5)
                .map(r -> new ProductRevenueItem(
                        (String) r[0],
                        (String) r[1],
                        ((Number) r[2]).longValue(),
                        ((Number) r[3]).doubleValue()
                ))
                .collect(Collectors.toList());

        return DashboardStatsResponse.builder()
                .totalOrders(totalOrders)
                .revenueTotal(revenueTotal != null ? revenueTotal : 0.0)
                .lowStockProducts(lowStock)
                .newCustomers(newCustomers)
                .chartRevenue(chartRevenue)
                .chartLabels(chartLabels)
                .categoryRevenue(categoryRevenue)
                .topProducts(topProducts)
                .period(period)
                .build();
    }

    private void buildChart(String period,
                            List<Double> revenues,
                            List<String> labels) {
        if ("day".equals(period)) {
            // 24 giờ hôm nay
            LocalDate today = LocalDate.now();
            for (int h = 0; h < 24; h += 3) { // mỗi 3 giờ = 8 cột
                LocalDateTime s = today.atTime(h, 0);
                LocalDateTime e = today.atTime(Math.min(h + 2, 23), 59, 59);
                Double rev = orderRepository.sumRevenueToday(s, e);
                revenues.add(rev != null ? rev : 0.0);
                labels.add(String.format("%02d:00", h));
            }
        } else if ("week".equals(period)) {
            LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
            String[] days = {"T2","T3","T4","T5","T6","T7","CN"};
            for (int i = 0; i < 7; i++) {
                LocalDate day = monday.plusDays(i);
                Double rev = orderRepository.sumRevenueToday(
                        day.atStartOfDay(), day.atTime(LocalTime.MAX));
                revenues.add(rev != null ? rev : 0.0);
                labels.add(days[i]);
            }
        } else { // month
            LocalDate first = LocalDate.now().withDayOfMonth(1);
            int daysInMonth = first.lengthOfMonth();
            for (int d = 1; d <= daysInMonth; d++) {
                LocalDate day = first.withDayOfMonth(d);
                Double rev = orderRepository.sumRevenueToday(
                        day.atStartOfDay(), day.atTime(LocalTime.MAX));
                revenues.add(rev != null ? rev : 0.0);
                labels.add(String.valueOf(d));
            }
        }
    }

    private LocalDateTime[] getRange(String period) {
        LocalDate today = LocalDate.now();
        return switch (period) {
            case "day"   -> new LocalDateTime[]{
                    today.atStartOfDay(),
                    today.atTime(LocalTime.MAX)
            };
            case "month" -> new LocalDateTime[]{
                    today.withDayOfMonth(1).atStartOfDay(),
                    today.withDayOfMonth(today.lengthOfMonth()).atTime(LocalTime.MAX)
            };
            default      -> new LocalDateTime[]{ // week
                    today.with(DayOfWeek.MONDAY).atStartOfDay(),
                    today.with(DayOfWeek.SUNDAY).atTime(LocalTime.MAX)
            };
        };
    }

    @Override
    public byte[] exportRevenuePdf(String period) throws Exception {
        DashboardStatsResponse stats = getStats(period);
        return revenueReportService.exportRevenuePdf(stats);
    }
}