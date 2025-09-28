package com.pap_shop.service;

import com.pap_shop.entity.Product;
import com.pap_shop.repository.CategoryRepository;
import com.pap_shop.repository.ProductRepository;
import com.pap_shop.repository.StockEntryRepository;
import com.pap_shop.util.ProductExcelImporter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Service class for importing products from Excel files.
 * Provides functionality to process and import product data from spreadsheets.
 */
@Service
@RequiredArgsConstructor
public class ProductImportService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StockEntryRepository stockEntryRepository;
    
    /**
     * Import products from Excel file.
     *
     * @param file the Excel file containing product data
     * @return list of imported products
     * @throws IOException if file processing fails
     */
    public List<Product> importProductsFromExcel(MultipartFile file) throws IOException {
        List<Product> products = ProductExcelImporter.importFromExcel(file, categoryRepository, productRepository, stockEntryRepository);
        return products;
    }
}