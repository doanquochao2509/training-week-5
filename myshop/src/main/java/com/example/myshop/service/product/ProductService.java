package com.example.myshop.service.product;

import com.example.myshop.dto.product.*;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ProductService {

    ProductResponse create(
            CreateProductRequest request);

    ProductResponse update(
            UUID id,
            UpdateProductRequest request);

    void changeStatus(
            UUID id,
            Boolean active);

    ProductResponse detail(
            UUID id);

    Page<ProductResponse> search(
            String keyword,
            int page,
            int size);
}