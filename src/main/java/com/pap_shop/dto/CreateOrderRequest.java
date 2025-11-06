package com.pap_shop.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateOrderRequest {
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
    
    private String notes;
}
