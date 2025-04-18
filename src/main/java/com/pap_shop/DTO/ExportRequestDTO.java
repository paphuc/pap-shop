package com.pap_shop.DTO;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for handling export product requests.
 * Contains the product ID, quantity to export, and an optional note.
 */
@Getter
@Setter
public class ExportRequestDTO {
    private Integer productId;  // ID of the product to export
    private Integer quantity;   // Quantity to export
    private String note;        // Optional note for the export
}
