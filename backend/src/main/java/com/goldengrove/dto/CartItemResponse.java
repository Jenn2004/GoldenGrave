package com.goldengrove.dto;

import com.goldengrove.entity.Product;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String imageUrl;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
    private Integer stock;

    public static CartItemResponse from(com.goldengrove.entity.CartItem item) {
        Product product = item.getProduct();
        BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(product.getId())
                .productName(product.getName())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .quantity(item.getQuantity())
                .subtotal(subtotal)
                .stock(product.getStock())
                .build();
    }
}
