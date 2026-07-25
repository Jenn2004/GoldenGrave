package com.goldengrove.dto;

import com.goldengrove.entity.Product;
import com.goldengrove.entity.WineType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Long categoryId;
    private String categoryName;
    private WineType wineType;
    private Integer vintageYear;
    private BigDecimal abv;
    private String imageUrl;
    private Boolean isFeatured;
    private Boolean isGiftEligible;
    private Double averageRating;
    private Long reviewCount;

    public static ProductResponse from(Product product) {
        return from(product, null, null);
    }

    public static ProductResponse from(Product product, Double averageRating, Long reviewCount) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .wineType(product.getWineType())
                .vintageYear(product.getVintageYear())
                .abv(product.getAbv())
                .imageUrl(product.getImageUrl())
                .isFeatured(product.getIsFeatured())
                .isGiftEligible(product.getIsGiftEligible())
                .averageRating(averageRating)
                .reviewCount(reviewCount)
                .build();
    }
}
