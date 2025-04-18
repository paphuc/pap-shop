package com.pap_shop.controller;

import com.pap_shop.service.StockService;
import com.pap_shop.DTO.ExportRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling stock-related operations.
 *
 * Provides endpoint to export products and update the corresponding stock.
 */
@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;

    /**
     * Constructor to inject the StockService.
     *
     * @param stockService the service responsible for stock operations
     */
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * Endpoint to export a product and update stock accordingly.
     *
     * @param request the export request containing productId and quantity
     * @return success message if export is successful
     */
    @PostMapping("/export")
    public ResponseEntity<String> exportProduct(@RequestBody ExportRequestDTO request) {
        stockService.exportProduct(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok("Export success");
    }
}
