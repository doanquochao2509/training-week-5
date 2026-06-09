package com.example.myshop.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private UUID id;

    private String code;

    private String name;

    private String description;

    private String imageUrl;

    private Double price;

    private Integer stockQuantity;

    private Boolean active;

    private UUID categoryId;

    private String categoryName;
}