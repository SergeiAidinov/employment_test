package ru.yandex.incoming34.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.yandex.incoming34.dto.DiapasonsOfNumbersWithTotalQuantity;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@org.springframework.stereotype.Service
public class Service {

    public String handleFile(String filePath, Integer order) {
        if (order < 1) throw new IllegalArgumentException("Order must be greater than 0");
        if (filePath == null || filePath.trim().isEmpty()) throw new IllegalArgumentException("File path cannot be empty");
        DiapasonsOfNumbersWithTotalQuantity qq = parseFile(filePath, order);

        return "Hello";
    }

    private DiapasonsOfNumbersWithTotalQuantity parseFile(String filePath, Integer order) {
        if (!Files.exists(Path.of(filePath))) throw new IllegalArgumentException("File not found");
        Map<Integer, List<Integer>> diapasonsWithNumbers = new TreeMap<>();
        int totalNumbers = 0;
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (cell.getCellType() == CellType.NUMERIC) {
                        totalNumbers++;
                        int currentValue = (int) cell.getNumericCellValue();
                        diapasonsWithNumbers.computeIfAbsent(currentValue/order, k -> new ArrayList<>()).add(currentValue);
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("File parsing error");
        }
        return new DiapasonsOfNumbersWithTotalQuantity(diapasonsWithNumbers, totalNumbers);
    }

}
