package com.cnf.helper;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cnf.entity.Product;
import com.cnf.services.CategoryService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;


public class ImportHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = {"active", "description", "discount", "img", "name", "price", "quantity", "weight", "category_id"};
    static String SHEET = "ProductImport";

    public static boolean hasExcelFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }

    public static ByteArrayInputStream tutorialsToExcel(List<Product> tutorials) {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET);

            // Header
            Row headerRow = sheet.createRow(0);

            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
            }

            int rowIdx = 1;
            for (Product tutorial : tutorials) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(tutorial.getName());
                row.createCell(1).setCellValue(tutorial.getDescription());
                row.createCell(2).setCellValue(tutorial.getPrice());
                row.createCell(3).setCellValue(tutorial.getQuantity());
                row.createCell(4).setCellValue(tutorial.getWeight());
                row.createCell(5).setCellValue(tutorial.getImg());
                row.createCell(6).setCellValue(tutorial.getCategory().getId());
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }

    public static List<Product> excelToTutorials(InputStream is) {
//        try {
//            Workbook workbook = new XSSFWorkbook(is);
//
//            Sheet sheet = workbook.getSheet(SHEET);
//            Iterator<Row> rows = sheet.iterator();
//
//            List<Product> tutorials = new ArrayList<Product>();
//
//            int rowNumber = 0;
//            while (rows.hasNext()) {
//                Row currentRow = rows.next();
//
//                // skip header
//                if (rowNumber == 0) {
//                    rowNumber++;
//                    continue;
//                }
//
//                Iterator<Cell> cellsInRow = currentRow.iterator();
//
//                Product tutorial = new Product();
//
//                int cellIdx = 0;
//                while (cellsInRow.hasNext()) {
//                    Cell currentCell = cellsInRow.next();
//
//                    switch (cellIdx) {
//
//                        case 0:
//                            tutorial.setName(currentCell.getStringCellValue());
//                            break;
//
//                        case 1:
//                            tutorial.setDescription(currentCell.getStringCellValue());
//                            break;
//
//                        case 2:
//                            tutorial.setPrice(currentCell.getNumericCellValue());
//                            break;
//                        case 3:
//                            tutorial.setQuantity((float) currentCell.getNumericCellValue());
//                            break;
//                        case 4:
//                            tutorial.setWeight((float) currentCell.getNumericCellValue());
//                            break;
//                        case 5:
//                            tutorial.setImg(currentCell.getStringCellValue());
//                            break;
//                        case 6:
//                            long categoryId = (long) currentCell.getNumericCellValue();
//                            CategoryService category = new CategoryService();
//                            tutorial.setCategory(category.findCategoryById(categoryId));
//                            break;
//                        default:
//                            break;
//                    }
//
//                    cellIdx++;
//                }
//
//                tutorials.add(tutorial);
//            }
//
//            workbook.close();
//
//            return tutorials;
//        } catch (IOException e) {
//            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
//        }
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEET);
            List<Product> products = new ArrayList<>();

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                Product product = new Product();
                product.setActive(row.getCell(0).getBooleanCellValue());
                product.setDescription(row.getCell(1).getStringCellValue());
                product.setDiscount((int) row.getCell(2).getNumericCellValue());
                product.setImg(row.getCell(3).getStringCellValue());
                product.setName(row.getCell(4).getStringCellValue());
                product.setPrice(row.getCell(5).getNumericCellValue());
                product.setQuantity((int) row.getCell(6).getNumericCellValue());
                product.setWeight((float) row.getCell(7).getNumericCellValue());
                long categoryId = (long) row.getCell(8).getNumericCellValue();
                CategoryService category = new CategoryService();
                product.setCategory(category.findCategoryById(categoryId));
                products.add(product);
            }

            workbook.close();
            return products;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
        }
    }
}
