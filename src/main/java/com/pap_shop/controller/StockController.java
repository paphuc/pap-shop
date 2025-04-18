package com.pap_shop.controller;

import com.pap_shop.service.StockService;
import com.pap_shop.DTO.ExportRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/export")
    public ResponseEntity<String> exportProduct(@RequestBody ExportRequestDTO request) {
        stockService.exportProduct(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok("Export success");
    }
}