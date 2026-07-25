package com.goldengrove.controller;

import com.goldengrove.dto.ProductResponse;
import com.goldengrove.entity.WineType;
import com.goldengrove.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Page<ProductResponse> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) WineType wineType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) Boolean giftEligible,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return productService.getProducts(categoryId, wineType, minPrice, maxPrice, search,
                featured, giftEligible, PageRequest.of(page, size, Sort.by("name")));
    }

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }
}
