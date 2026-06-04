package com.example.myshop.service.category;

import com.example.myshop.dto.category.*;
import com.example.myshop.entity.Category;
import com.example.myshop.exception.BadRequestException;
import com.example.myshop.exception.ResourceNotFoundException;
import com.example.myshop.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl
        implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse create(
            CreateCategoryRequest request) {

        if (categoryRepository.existsByCode(
                request.getCode())) {

            throw new BadRequestException(
                    "Mã danh mục đã tồn tại");
        }

        Category category = new Category();

        category.setCode(
                request.getCode().trim());

        category.setName(
                request.getName().trim());

        category.setDescription(
                request.getDescription());

        category.setActive(true);

        categoryRepository.save(category);

        return mapToResponse(category);
    }

    @Override
    public CategoryResponse update(
            UUID id,
            UpdateCategoryRequest request) {

        Category category =
                categoryRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Danh mục không tồn tại"));

        category.setName(
                request.getName());

        category.setDescription(
                request.getDescription());

        categoryRepository.save(category);

        return mapToResponse(category);
    }

    @Override
    public void changeStatus(
            UUID id,
            Boolean active) {

        Category category =
                categoryRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Danh mục không tồn tại"));

        category.setActive(active);

        categoryRepository.save(category);
    }

    @Override
    public CategoryResponse detail(
            UUID id) {

        Category category =
                categoryRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Danh mục không tồn tại"));

        return mapToResponse(category);
    }

    @Override
    public Page<CategoryResponse> search(
            String keyword,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("createdDate")
                                .descending());

        return categoryRepository
                .search(keyword, pageable)
                .map(this::mapToResponse);
    }

    private CategoryResponse mapToResponse(
            Category category) {

        return CategoryResponse.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.getActive())
                .build();
    }
}