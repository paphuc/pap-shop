package com.pap_shop.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ImportStockRequest {
    private Integer productId;
    private int quantity;
    private BigDecimal purchasePrice;
    private LocalDate entryDate;
}
