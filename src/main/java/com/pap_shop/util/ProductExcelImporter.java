package com.pap_shop.util;

import com.pap_shop.entity.Category;
import com.pap_shop.entity.Product;
import com.pap_shop.entity.StockEntry;
import com.pap_shop.repository.CategoryRepository;
import com.pap_shop.repository.ProductRepository;
import com.pap_shop.repository.StockEntryRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProductExcelImporter {
    
    public static List<Product> importFromExcel(MultipartFile file, CategoryRepository categoryRepository, 
                                               ProductRepository productRepository, StockEntryRepository stockEntryRepository) throws IOException {
        List<Product> products = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Skip header row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                Product product = new Product();
                
                // SKU (Column A)
                Cell skuCell = row.getCell(0);
                if (skuCell != null) {
                    product.setSku(getCellValueAsString(skuCell));
                }
                
                // Name (Column B)
                Cell nameCell = row.getCell(1);
                if (nameCell != null) {
                    product.setName(getCellValueAsString(nameCell));
                }
                
                // Category ID (Column C)
                Cell categoryCell = row.getCell(2);
                if (categoryCell != null) {
                    int categoryId = (int) categoryCell.getNumericCellValue();
                    Category category = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
                    product.setCategory(category);
                }
                
                // Description (Column D)
                Cell descCell = row.getCell(3);
                if (descCell != null) {
                    product.setDescription(getCellValueAsString(descCell));
                }
                
                // Price (Column E)
                Cell priceCell = row.getCell(4);
                if (priceCell != null) {
                    product.setPrice(BigDecimal.valueOf(priceCell.getNumericCellValue()));
                }
                
                // Stock (Column F)
                Cell stockCell = row.getCell(5);
                if (stockCell != null) {
                    product.setStock((int) stockCell.getNumericCellValue());
                }
                
                // Purchase Price (Column G)
                Cell purchasePriceCell = row.getCell(6);
                
                // Supplier (Column H) - Create StockEntry
                Cell supplierCell = row.getCell(7);
                if (supplierCell != null && stockCell != null && purchasePriceCell != null) {
                    Product savedProduct = productRepository.save(product);
                    
                    StockEntry stockEntry = new StockEntry();
                    stockEntry.setProduct(savedProduct);
                    stockEntry.setStock((int) stockCell.getNumericCellValue());
                    stockEntry.setPurchasePrice(BigDecimal.valueOf(purchasePriceCell.getNumericCellValue()));
                    stockEntry.setSupplier(getCellValueAsString(supplierCell));
                    stockEntry.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    
                    stockEntryRepository.save(stockEntry);
                }
                
                products.add(product);
            }
        }
        
        return products;
    }
    
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}