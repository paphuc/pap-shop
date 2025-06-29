package com.pap_shop.util;

import com.pap_shop.entity.Product;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
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
        String sheetName = "Products";
        String mainTitle = "PRODUCT LIST";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sheetName);

            // --- Start of Style Creation ---

            // Style for the main title (Centered, Bold, Large Font)
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Style for column headers (Centered, Bold, Uppercase, with Borders)
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            // Add borders
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Style for data cells (With Borders)
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // --- End of Style Creation ---


            // --- Start of Row Creation ---

            // Row 0: Main Title
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(mainTitle);
            titleCell.setCellStyle(titleStyle);
            // Merge cells for the main title
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columns.length - 1));
            titleRow.setHeightInPoints(20); // Increase the title row height

            // Row 1: Column Headers
            // Shifted down to row index 1 (instead of 0)
            Row headerRow = sheet.createRow(1);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i].toUpperCase()); // UPPERCASE
                cell.setCellStyle(headerStyle);
            }

            // Row 2 onwards: Data
            // Start from rowIdx = 2
            int rowIdx = 2;
            for (Product product : products) {
                Row row = sheet.createRow(rowIdx++);

                // Create cells and apply the data style
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(product.getId());
                cell0.setCellStyle(dataStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(product.getName());
                cell1.setCellStyle(dataStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(product.getCategory().getName());
                cell2.setCellStyle(dataStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(product.getDescription());
                cell3.setCellStyle(dataStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(product.getPrice().doubleValue());
                cell4.setCellStyle(dataStyle);

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(product.getStock());
                cell5.setCellStyle(dataStyle);

                Cell cell6 = row.createCell(6);
                LocalDateTime createdAt = product.getCreatedAt();
                if (createdAt != null) {
                    String formattedDate = createdAt.format(formatter);
                    cell6.setCellValue(formattedDate);
                } else {
                    cell6.setCellValue("");
                }
                cell6.setCellStyle(dataStyle);
            }

            // Auto-size columns to fit the content
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}