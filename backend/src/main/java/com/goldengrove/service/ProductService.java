package com.goldengrove.service;

import com.goldengrove.dto.ProductRequest;
import com.goldengrove.dto.ProductResponse;
import com.goldengrove.entity.Category;
import com.goldengrove.entity.Product;
import com.goldengrove.entity.WineType;
import com.goldengrove.exception.ResourceNotFoundException;
import com.goldengrove.repository.CategoryRepository;
import com.goldengrove.repository.ProductRepository;
import com.goldengrove.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;

    public Page<ProductResponse> getProducts(
            Long categoryId, WineType wineType, BigDecimal minPrice, BigDecimal maxPrice,
            String search, Boolean featured, Boolean giftEligible, Pageable pageable) {

        Page<Product> page;
        if (Boolean.TRUE.equals(featured)) {
            page = productRepository.findByIsFeaturedTrue(pageable);
        } else if (Boolean.TRUE.equals(giftEligible)) {
            page = productRepository.findByIsGiftEligibleTrue(pageable);
        } else {
            page = productRepository.searchProducts(categoryId, wineType, minPrice, maxPrice, search, pageable);
        }
        return page.map(this::toResponseWithRating);
    }

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return toResponseWithRating(product);
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = mapRequest(new Product(), request);
        return toResponseWithRating(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        mapRequest(product, request);
        return toResponseWithRating(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }

    private Product mapRequest(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setWineType(request.getWineType());
        product.setVintageYear(request.getVintageYear());
        product.setAbv(request.getAbv());
        product.setImageUrl(request.getImageUrl());
        product.setIsFeatured(Boolean.TRUE.equals(request.getIsFeatured()));
        product.setIsGiftEligible(Boolean.TRUE.equals(request.getIsGiftEligible()));
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            product.setCategory(category);
        }
        return product;
    }

    private ProductResponse toResponseWithRating(Product product) {
        var reviews = reviewRepository.findByProductOrderByCreatedAtDesc(product);
        Double avg = reviews.isEmpty() ? null :
                reviews.stream().mapToInt(r -> r.getRating()).average().orElse(0);
        return ProductResponse.from(product, avg, (long) reviews.size());
    }
}
