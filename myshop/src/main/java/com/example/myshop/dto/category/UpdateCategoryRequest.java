package com.example.myshop.dto.category;

import lombok.Data;

@Data
public class UpdateCategoryRequest {

    private String name;

    private String description;
}