package com.example.myshop.dto.category;

import lombok.Data;

@Data
public class CreateCategoryRequest {

    private String code;

    private String name;

    private String description;
}