package com.example.myshop.dto.product;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateProductRequest {

    private String name;

    private String description;

    private String imageUrl;

    private Double price;

    private Integer stockQuantity;

    private UUID categoryId;
}