package com.example.myshop.dto.customer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCustomerRequest {
    private String customerName;
    private String phone;
    private String email;
    private String address;
}