package com.goldengrove.service;

import com.goldengrove.dto.CartItemRequest;
import com.goldengrove.dto.CartItemResponse;
import com.goldengrove.entity.CartItem;
import com.goldengrove.entity.Product;
import com.goldengrove.entity.User;
import com.goldengrove.exception.BadRequestException;
import com.goldengrove.exception.ResourceNotFoundException;
import com.goldengrove.repository.CartItemRepository;
import com.goldengrove.repository.ProductRepository;
import com.goldengrove.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<CartItemResponse> getCart(Long userId) {
        User user = getUser(userId);
        return cartItemRepository.findByUser(user).stream()
                .map(CartItemResponse::from)
                .toList();
    }

    @Transactional
    public CartItemResponse addToCart(Long userId, CartItemRequest request) {
        User user = getUser(userId);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (product.getStock() < request.getQuantity()) {
            throw new BadRequestException("Insufficient stock");
        }
        CartItem item = cartItemRepository.findByUserAndProductId(user, product.getId())
                .orElse(CartItem.builder().user(user).product(product).quantity(0).build());
        int newQty = item.getQuantity() + request.getQuantity();
        if (newQty > product.getStock()) {
            throw new BadRequestException("Insufficient stock");
        }
        item.setQuantity(newQty);
        return CartItemResponse.from(cartItemRepository.save(item));
    }

    @Transactional
    public CartItemResponse updateQuantity(Long userId, Long itemId, Integer quantity) {
        User user = getUser(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!item.getUser().getId().equals(userId)) {
            throw new BadRequestException("Not your cart item");
        }
        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return null;
        }
        if (quantity > item.getProduct().getStock()) {
            throw new BadRequestException("Insufficient stock");
        }
        item.setQuantity(quantity);
        return CartItemResponse.from(cartItemRepository.save(item));
    }

    @Transactional
    public void removeItem(Long userId, Long itemId) {
        User user = getUser(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!item.getUser().getId().equals(userId)) {
            throw new BadRequestException("Not your cart item");
        }
        cartItemRepository.delete(item);
    }

    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
