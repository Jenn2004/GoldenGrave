package com.goldengrove.dto;

import com.goldengrove.entity.Order;
import com.goldengrove.entity.OrderItem;
import com.goldengrove.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private Boolean isGift;
    private String giftMessage;
    private String recipientName;
    private String shippingName;
    private String shippingPhone;
    private String addressLine1;
    private String city;
    private String state;
    private String pincode;
    private String razorpayOrderId;
    private String razorpayKeyId;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    @Data
    @Builder
    public static class OrderItemResponse {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal priceAtPurchase;
    }

    public static OrderResponse from(Order order) {
        return from(order, null);
    }

    public static OrderResponse from(Order order, String razorpayKeyId) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(OrderResponse::mapItem)
                .toList();
        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .isGift(order.getIsGift())
                .giftMessage(order.getGiftMessage())
                .recipientName(order.getRecipientName())
                .shippingName(order.getShippingName())
                .shippingPhone(order.getShippingPhone())
                .addressLine1(order.getAddressLine1())
                .city(order.getCity())
                .state(order.getState())
                .pincode(order.getPincode())
                .razorpayOrderId(order.getRazorpayOrderId())
                .razorpayKeyId(razorpayKeyId)
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }

    private static OrderItemResponse mapItem(OrderItem item) {
        return OrderItemResponse.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .priceAtPurchase(item.getPriceAtPurchase())
                .build();
    }
}
