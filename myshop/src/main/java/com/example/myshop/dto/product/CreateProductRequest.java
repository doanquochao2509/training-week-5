package com.example.myshop.dto.product;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateProductRequest {

    private String code;

    private String name;

    private String description;

    private Double price;

    private Integer stockQuantity;

    private UUID categoryId;
}