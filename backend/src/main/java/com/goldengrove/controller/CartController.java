package com.goldengrove.controller;

import com.goldengrove.dto.CartItemRequest;
import com.goldengrove.dto.CartItemResponse;
import com.goldengrove.service.CartService;
import com.goldengrove.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public List<CartItemResponse> getCart() {
        return cartService.getCart(SecurityUtils.getCurrentUser().getId());
    }

    @PostMapping
    public CartItemResponse addToCart(@Valid @RequestBody CartItemRequest request) {
        return cartService.addToCart(SecurityUtils.getCurrentUser().getId(), request);
    }

    @PutMapping("/{itemId}")
    public CartItemResponse updateQuantity(
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> body) {
        return cartService.updateQuantity(
                SecurityUtils.getCurrentUser().getId(), itemId, body.get("quantity"));
    }

    @DeleteMapping("/{itemId}")
    public void removeItem(@PathVariable Long itemId) {
        cartService.removeItem(SecurityUtils.getCurrentUser().getId(), itemId);
    }
}
