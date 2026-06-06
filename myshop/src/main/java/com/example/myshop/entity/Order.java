package com.example.myshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity {

    @Column(name = "order_code", unique = true, nullable = false)
    private String orderCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // Có thể map với Entity Customer nếu bạn đã viết, ở đây để UUID theo DB của bạn

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "status", nullable = false)
    private String status; // PENDING, CONFIRMED, COMPLETED, CANCELLED

    @Column(name = "note")
    private String note;

    @Column(name = "created_by")
    private String createdBy; // Lưu username hoặc danh tính người tạo đơn

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails = new ArrayList<>();
}