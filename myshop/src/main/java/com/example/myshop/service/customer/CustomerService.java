package com.example.myshop.service.customer;

import com.example.myshop.dto.customer.*;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface CustomerService {

    CustomerResponse create(CreateCustomerRequest request);

    CustomerResponse update(UUID id, UpdateCustomerRequest request);

    void changeStatus(UUID id, Boolean active);

    CustomerResponse detail(UUID id);

    Page<CustomerResponse> search(String keyword, int page, int size);
}