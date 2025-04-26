package com.pap_shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Request body for exporting multiple products.
 */
@Getter
@Setter
public class ExportProductRequest {

    private List<Item> items;
    private String note;

    /**
     * Represents a single product and quantity to export.
     */
    @Getter
    @Setter
    public static class Item {
        private Integer productId;
        private Integer quantity;
    }
}
