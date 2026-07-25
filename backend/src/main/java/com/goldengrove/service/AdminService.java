package com.goldengrove.service;

import com.goldengrove.dto.DashboardResponse;
import com.goldengrove.dto.ProductResponse;
import com.goldengrove.dto.UserResponse;
import com.goldengrove.entity.OrderStatus;
import com.goldengrove.entity.User;
import com.goldengrove.exception.ResourceNotFoundException;
import com.goldengrove.repository.OrderRepository;
import com.goldengrove.repository.ProductRepository;
import com.goldengrove.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public DashboardResponse getDashboard() {
        List<ProductResponse> lowStock = productRepository.findByStockLessThanEqual(5).stream()
                .map(ProductResponse::from)
                .toList();
        return DashboardResponse.builder()
                .totalSales(orderRepository.totalSales())
                .totalOrders(orderRepository.count())
                .pendingOrders(orderRepository.countByStatus(OrderStatus.PAYMENT_PENDING))
                .lowStockCount(lowStock.size())
                .lowStockProducts(lowStock)
                .build();
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .toList();
    }

    @Transactional
    public UserResponse setUserEnabled(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setEnabled(enabled);
        return toUserResponse(userRepository.save(user));
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .enabled(user.getEnabled())
                .build();
    }
}
