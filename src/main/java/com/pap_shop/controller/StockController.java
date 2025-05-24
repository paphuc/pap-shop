package com.pap_shop.controller;

import com.pap_shop.dto.ImportStockRequest;
import com.pap_shop.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping("/import")
    public ResponseEntity<?> importStock(@RequestBody ImportStockRequest request) {
        stockService.importStock(request);
        return ResponseEntity.status(201).body("Stock entry created successfully");
    }
}
