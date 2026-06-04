package com.example.myshop.dto.customer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CustomerResponse {
    private UUID id;
    private String customerCode;
    private String customerName;
    private String phone;
    private String email;
    private String address;
    private Boolean active;
}