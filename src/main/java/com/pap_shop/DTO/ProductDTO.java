package com.pap_shop.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) for transferring product data.
 * This DTO is used to transfer product information such as name, category, description, price, and stock.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    /**
     * The name of the product.
     */
    private String name;

    /**
     * The ID of the category to which the product belongs.
     */
    private Integer category;

    /**
     * A detailed description of the product.
     */
    private String description;

    /**
     * The price of the product.
     * Stored as a BigDecimal with a precision of 10 digits and scale of 2 digits after the decimal point.
     */
    private BigDecimal price;

    /**
     * The number of items available in stock for this product.
     */
    private Integer stock;
}
