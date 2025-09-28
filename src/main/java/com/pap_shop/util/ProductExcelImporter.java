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
import java.util.Optional;

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
                
                // SKU (Column A)
                String sku = "";
                Cell skuCell = row.getCell(0);
                if (skuCell != null) {
                    sku = getCellValueAsString(skuCell).trim();
                }
                
                // Name (Column B)
                String name = "";
                Cell nameCell = row.getCell(1);
                if (nameCell != null) {
                    name = getCellValueAsString(nameCell).trim();
                }
                
                // Category ID (Column C)
                Category category = null;
                Cell categoryCell = row.getCell(2);
                if (categoryCell != null) {
                    int categoryId = (int) categoryCell.getNumericCellValue();
                    category = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
                }
                
                // Generate SKU if empty
                if (sku.isEmpty() && !name.isEmpty() && category != null) {
                    sku = generateSku(name, category.getName());
                }
                
                // Check if product with same SKU and name exists
                Optional<Product> existingProduct = productRepository.findBySku(sku);
                Product product;
                
                if (existingProduct.isPresent() && existingProduct.get().getName().equals(name)) {
                    // Update existing product stock
                    product = existingProduct.get();
                    Cell stockCell = row.getCell(5);
                    if (stockCell != null) {
                        int additionalStock = (int) stockCell.getNumericCellValue();
                        product.setStock(product.getStock() + additionalStock);
                    }
                } else {
                    // Create new product
                    product = new Product();
                    product.setSku(sku);
                    product.setName(name);
                    product.setCategory(category);
                    
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
                }
                
                // Save product
                Product savedProduct = productRepository.save(product);
                
                // Purchase Price (Column G) & Supplier (Column H) - Create StockEntry
                Cell purchasePriceCell = row.getCell(6);
                Cell supplierCell = row.getCell(7);
                Cell stockCell = row.getCell(5);
                
                if (supplierCell != null && stockCell != null && purchasePriceCell != null) {
                    StockEntry stockEntry = new StockEntry();
                    stockEntry.setProduct(savedProduct);
                    stockEntry.setStock((int) stockCell.getNumericCellValue());
                    stockEntry.setPurchasePrice(BigDecimal.valueOf(purchasePriceCell.getNumericCellValue()));
                    stockEntry.setSupplier(getCellValueAsString(supplierCell));
                    stockEntry.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    
                    stockEntryRepository.save(stockEntry);
                }
                
                products.add(savedProduct);
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
    
    private static String generateSku(String productName, String categoryName) {
        String prefix = removeDiacritics(categoryName).replaceAll("[^a-zA-Z]", "").substring(0, 1).toUpperCase();
        long number = System.currentTimeMillis() % 100000;
        return prefix + String.format("%05d", number);
    }
    
    private static String removeDiacritics(String input) {
        return input.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                   .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                   .replaceAll("[ìíịỉĩ]", "i")
                   .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                   .replaceAll("[ùúụủũưừứựửữ]", "u")
                   .replaceAll("[ỳýỵỷỹ]", "y")
                   .replaceAll("[đ]", "d")
                   .replaceAll("[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]", "A")
                   .replaceAll("[ÈÉẸẺẼÊỀẾỆỂỄ]", "E")
                   .replaceAll("[ÌÍỊỈĨ]", "I")
                   .replaceAll("[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]", "O")
                   .replaceAll("[ÙÚỤỦŨƯỪỨỰỬỮ]", "U")
                   .replaceAll("[ỲÝỴỶỸ]", "Y")
                   .replaceAll("[Đ]", "D");
    }
}