package com.pap_shop.util;

import com.pap_shop.entity.Product;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProductExcelExporter {

    public static ByteArrayInputStream export(List<Product> products) throws IOException {
        String[] columns = {"ID", "Name", "Category", "Description", "Price", "Stock", "Created At"};

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Products");

            // Header
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Data
            int rowIdx = 1;
            for (Product product : products) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getName());
                row.createCell(2).setCellValue(product.getCategory().getName());
                row.createCell(3).setCellValue(product.getDescription());
                row.createCell(4).setCellValue(product.getPrice().doubleValue());
                row.createCell(5).setCellValue(product.getStock());

                LocalDateTime createdAt = product.getCreatedAt();

                if (createdAt != null) {

                    String formattedDate = createdAt.format(formatter);
                    row.createCell(6).setCellValue(formattedDate);
                } else {
                    row.createCell(6).setCellValue("");
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}