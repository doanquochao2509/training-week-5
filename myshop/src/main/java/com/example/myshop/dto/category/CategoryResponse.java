package com.example.myshop.dto.category;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private UUID id;

    private String code;

    private String name;

    private String description;

    private Boolean active;
}