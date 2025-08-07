package com.pap_shop.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProductExcelTemplate {
    
    public static ByteArrayInputStream generateTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"SKU", "Name", "Category ID", "Description", "Price", "Stock", "Purchase Price", "Supplier"};
            
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }
            
            // Add sample data row
            Row sampleRow = sheet.createRow(1);
            sampleRow.createCell(0).setCellValue("SAMPLE001");
            sampleRow.createCell(1).setCellValue("Sample Product");
            sampleRow.createCell(2).setCellValue(1);
            sampleRow.createCell(3).setCellValue("Sample description");
            sampleRow.createCell(4).setCellValue(99.99);
            sampleRow.createCell(5).setCellValue(100);
            sampleRow.createCell(6).setCellValue(50.00);
            sampleRow.createCell(7).setCellValue("ABC Supplier Co.");
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}