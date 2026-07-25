package com.goldengrove.dto;

import com.goldengrove.entity.WineType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank private String name;
    private String description;
    @NotNull @DecimalMin("0.01") private BigDecimal price;
    @NotNull private Integer stock;
    private Long categoryId;
    @NotNull private WineType wineType;
    private Integer vintageYear;
    private BigDecimal abv;
    private String imageUrl;
    private Boolean isFeatured;
    private Boolean isGiftEligible;
}
