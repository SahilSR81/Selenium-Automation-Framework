package com.saf.utils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
public class ExcelUtil {
/*
 * Reads data from an Excel sheet.
 * Skips the header row (row 0).
 * Returns data as String[][].
 */
public static String[][] readExcelData(String filePath, String sheetName) {
    try (FileInputStream fis = new FileInputStream(filePath);
         Workbook wb = new XSSFWorkbook(fis)) {

        Sheet sheet = wb.getSheet(sheetName);
        if (sheet == null) {
            System.err.println("[ExcelUtil] Sheet '" + sheetName + "' not found");
            return new String[0][0];
        }
        int rows = sheet.getLastRowNum();      // excludes header
        if (rows == 0) {
            System.err.println("[ExcelUtil] Sheet '" + sheetName + "' has no data rows");
            return new String[0][0];
        }
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            System.err.println("[ExcelUtil] Sheet '" + sheetName + "' has no header row");
            return new String[0][0];
        }
        int cols = headerRow.getLastCellNum();
        if (cols == 0) {
            System.err.println("[ExcelUtil] Sheet '" + sheetName + "' has no columns");
            return new String[0][0];
        }
        DataFormatter fmt = new DataFormatter();
        String[][] data = new String[rows][cols];

        for (int r = 1; r <= rows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;
            for (int c = 0; c < cols; c++) {
                Cell cell = row.getCell(c);
                data[r - 1][c] = (cell == null) ? "" : fmt.formatCellValue(cell);
            }
        }
        System.out.println("[ExcelUtil] Read " + rows + " rows from " + sheetName);
        return data;

    } catch (Exception e) {
        // Log any exceptions and return empty data
        System.err.println("[ExcelUtil] Read error: " + e.getMessage());
        return new String[0][0];
    }
}

/*
 * Writes a test result string into an Excel cell.
 * Appends to the last column of the given row.
 */
public static void writeResult(String filePath, String sheetName,
                               int rowNum, String result) {
    try (FileInputStream fis = new FileInputStream(filePath);
         Workbook wb = new XSSFWorkbook(fis)) {

        Sheet sheet = wb.getSheet(sheetName);
        Row row = sheet.getRow(rowNum);
        if (row == null) row = sheet.createRow(rowNum);
        int nextCol = (row.getLastCellNum() < 0) ? 0 : row.getLastCellNum();
        row.createCell(nextCol).setCellValue(result);

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            wb.write(fos);
        }
        System.out.println("[ExcelUtil] Result written: row=" + rowNum
            + " col=" + nextCol + " value=" + result);

    } catch (Exception e) {
        // Log any exceptions during write operation
        System.err.println("[ExcelUtil] Write error: " + e.getMessage());
    }
}

/*
 * Creates testdata.xlsx file if it's missing or empty.
 * Includes sample login test data.
 */
public static void createTestDataFileIfMissing(String filePath) {
    File f = new File(filePath);
    if (f.exists() && f.length() > 0) {
        System.out.println("[ExcelUtil] testdata.xlsx exists, skipping creation");
        return;
    }
    
    // Create testdata.xlsx file if it's missing or empty
    try (Workbook wb = new XSSFWorkbook()) {
        Sheet sheet = wb.createSheet("LoginData");
        String[][] data = {
            {"Email", "Password", "ExpectedResult"},
            {"wrong1@test.com", "badpass1", "Fail"},
            {"invalid2@fake.com", "badpass2", "Fail"},
            {"nouser3@test.com", "badpass3", "Fail"}
        };
        
        for (int r = 0; r < data.length; r++) {
            Row row = sheet.createRow(r);
            for (int c = 0; c < data[r].length; c++) {
                row.createCell(c).setCellValue(data[r][c]);
            }
        }
        
        new File(filePath).getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            wb.write(fos);
        }
        System.out.println("[ExcelUtil] Created testdata.xlsx at: " + filePath);
        
    } catch (Exception e) {
        // Log any exceptions during file creation
        System.err.println("[ExcelUtil] Create error: " + e.getMessage());
    }
}
}
