package com.pap_shop.service;

import com.pap_shop.repository.ProductRepository;
import com.pap_shop.entity.Product;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling product stock export logic.
 * Includes checking current stock and updating the stock after export.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockService {

    ProductRepository productRepository;

    /**
     * Export Product by its ID
     *
     * @param productID the ID of the product to search for
     * @param quantity quantity of product to export
     */
    public void exportProduct(Integer productID, Integer quantity){
        Product product = productRepository.findById(productID)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if(product.getStock() < quantity){
            throw  new RuntimeException("Not enough stock to export");
        }
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
}
