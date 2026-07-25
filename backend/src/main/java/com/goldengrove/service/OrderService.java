package com.goldengrove.service;

import com.goldengrove.dto.CheckoutRequest;
import com.goldengrove.dto.OrderResponse;
import com.goldengrove.dto.PaymentVerifyRequest;
import com.goldengrove.entity.*;
import com.goldengrove.exception.BadRequestException;
import com.goldengrove.exception.ResourceNotFoundException;
import com.goldengrove.repository.CartItemRepository;
import com.goldengrove.repository.OrderRepository;
import com.goldengrove.repository.UserRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    @Value("${app.razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${app.razorpay.key-secret}")
    private String razorpayKeySecret;

    public List<OrderResponse> getUserOrders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(OrderResponse::from)
                .toList();
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional
    public OrderResponse checkout(Long userId, CheckoutRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        BigDecimal total = BigDecimal.ZERO;
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PAYMENT_PENDING)
                .isGift(Boolean.TRUE.equals(request.getIsGift()))
                .giftMessage(request.getGiftMessage())
                .recipientName(request.getRecipientName())
                .shippingName(request.getShippingName())
                .shippingPhone(request.getShippingPhone())
                .addressLine1(request.getAddressLine1())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .build();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new BadRequestException("Insufficient stock for " + product.getName());
            }
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            total = total.add(lineTotal);
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .build();
            order.getItems().add(orderItem);
        }
        order.setTotalAmount(total);

        try {
            if (!razorpayKeySecret.contains("placeholder")) {
                RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
                JSONObject options = new JSONObject();
                options.put("amount", total.multiply(BigDecimal.valueOf(100)).intValue());
                options.put("currency", "INR");
                options.put("receipt", "order_" + System.currentTimeMillis());
                com.razorpay.Order razorpayOrder = client.orders.create(options);
                order.setRazorpayOrderId(razorpayOrder.get("id"));
            } else {
                order.setRazorpayOrderId("demo_order_" + System.currentTimeMillis());
            }
        } catch (RazorpayException e) {
            throw new BadRequestException("Payment initialization failed: " + e.getMessage());
        }

        orderRepository.save(order);
        return OrderResponse.from(order, razorpayKeyId);
    }

    @Transactional
    public OrderResponse verifyPayment(Long userId, PaymentVerifyRequest request) {
        Order order = orderRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("Not your order");
        }
        if (order.getStatus() == OrderStatus.PAID) {
            return OrderResponse.from(order);
        }

        if (!razorpayKeySecret.contains("placeholder")) {
            if (!verifySignature(request.getRazorpayOrderId(), request.getRazorpayPaymentId(),
                    request.getRazorpaySignature())) {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                throw new BadRequestException("Invalid payment signature");
            }
        }

        order.setRazorpayPaymentId(request.getRazorpayPaymentId());
        order.setStatus(OrderStatus.PAID);

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
        }

        orderRepository.save(order);
        cartService.clearCart(order.getUser());
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse demoPay(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("Not your order");
        }
        if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {
            throw new BadRequestException("Order is not pending payment");
        }
        order.setRazorpayPaymentId("demo_pay_" + System.currentTimeMillis());
        order.setStatus(OrderStatus.PAID);
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
        }
        orderRepository.save(order);
        cartService.clearCart(order.getUser());
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        return OrderResponse.from(orderRepository.save(order));
    }

    private boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expected = HexFormat.of().formatHex(hash);
            return expected.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
}
