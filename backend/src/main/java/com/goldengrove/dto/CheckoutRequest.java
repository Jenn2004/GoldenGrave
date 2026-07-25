package com.goldengrove.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotBlank private String shippingName;
    @NotBlank private String shippingPhone;
    @NotBlank private String addressLine1;
    @NotBlank private String city;
    @NotBlank private String state;
    @NotBlank private String pincode;
    private Boolean isGift;
    private String giftMessage;
    private String recipientName;
}
