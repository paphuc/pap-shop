package com.pap_shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateProductRequest {
    private String name;
    private Integer category;
    private String description;
    private BigDecimal price;
    private Integer stock;
}