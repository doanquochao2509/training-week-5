package com.example.myshop.service.product;

import com.example.myshop.dto.product.*;
import com.example.myshop.entity.Category;
import com.example.myshop.entity.Product;
import com.example.myshop.exception.BadRequestException;
import com.example.myshop.exception.ResourceNotFoundException;
import com.example.myshop.repository.CategoryRepository;
import com.example.myshop.repository.ProductRepository;
import com.example.myshop.storage.FileStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl
        implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    @Override
    public ProductResponse create(
            CreateProductRequest request) {

        if (productRepository.existsByCode(
                request.getCode())) {

            throw new BadRequestException(
                    "Mã sản phẩm đã tồn tại");
        }

        Category category =
                categoryRepository
                        .findById(request.getCategoryId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Danh mục không tồn tại"));

        Product product = new Product();

        product.setCode(
                request.getCode().trim());

        product.setName(
                request.getName().trim());

        product.setDescription(
                request.getDescription());

        product.setPrice(
                request.getPrice());

        product.setStockQuantity(
                request.getStockQuantity());

        product.setCategory(category);

        product.setActive(true);

        productRepository.save(product);

        return mapToResponse(product);
    }

    @Override
    public ProductResponse update(
            UUID id,
            UpdateProductRequest request) {

        Product product =
                productRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Sản phẩm không tồn tại"));

        Category category =
                categoryRepository
                        .findById(request.getCategoryId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Danh mục không tồn tại"));

        product.setName(
                request.getName());

        product.setDescription(
                request.getDescription());

        product.setPrice(
                request.getPrice());

        product.setStockQuantity(
                request.getStockQuantity());

        product.setCategory(category);

        productRepository.save(product);

        return mapToResponse(product);
    }

    @Override
    public void changeStatus(
            UUID id,
            Boolean active) {

        Product product =
                productRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Sản phẩm không tồn tại"));

        product.setActive(active);

        productRepository.save(product);
    }

    @Override
    public ProductResponse detail(
            UUID id) {

        Product product =
                productRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Sản phẩm không tồn tại"));

        return mapToResponse(product);
    }

    @Override
    public Page<ProductResponse> search(
            String keyword,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("createdDate")
                                .descending());

        return productRepository
                .search(keyword, pageable)
                .map(this::mapToResponse);
    }

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public ProductResponse uploadImage(UUID id, MultipartFile file) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));

        // Xóa ảnh cũ nếu có
        if (product.getImageUrl() != null) {
            String oldFileName = product.getImageUrl()
                    .replace(baseUrl + "/uploads/", "");
            fileStorageService.deleteFile(oldFileName);
        }

        String fileName = fileStorageService.storeFile(file);
        product.setImageUrl(baseUrl + "/uploads/" + fileName);
        productRepository.save(product);

        return mapToResponse(product);
    }

    @Override
    public void deleteImage(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));

        if (product.getImageUrl() != null) {
            String oldFileName = product.getImageUrl()
                    .replace(baseUrl + "/uploads/", "");
            fileStorageService.deleteFile(oldFileName);
            product.setImageUrl(null);
            productRepository.save(product);
        }
    }

    private ProductResponse mapToResponse(
            Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .active(product.getActive())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .build();
    }
}