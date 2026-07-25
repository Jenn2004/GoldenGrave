package com.goldengrove.controller;

import com.goldengrove.dto.CheckoutRequest;
import com.goldengrove.dto.OrderResponse;
import com.goldengrove.dto.PaymentVerifyRequest;
import com.goldengrove.service.OrderService;
import com.goldengrove.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderResponse> getMyOrders() {
        return orderService.getUserOrders(SecurityUtils.getCurrentUser().getId());
    }

    @PostMapping("/checkout")
    public OrderResponse checkout(@Valid @RequestBody CheckoutRequest request) {
        return orderService.checkout(SecurityUtils.getCurrentUser().getId(), request);
    }

    @PostMapping("/verify-payment")
    public OrderResponse verifyPayment(@Valid @RequestBody PaymentVerifyRequest request) {
        return orderService.verifyPayment(SecurityUtils.getCurrentUser().getId(), request);
    }

    @PostMapping("/{orderId}/demo-pay")
    public OrderResponse demoPay(@PathVariable Long orderId) {
        return orderService.demoPay(SecurityUtils.getCurrentUser().getId(), orderId);
    }
}
