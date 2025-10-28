package ru.yandex.incoming34.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.yandex.incoming34.dto.DiapasonsOfNumbersWithTotalQuantity;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class Service {

    public String handleFile(String filePath, Integer order) throws IllegalArgumentException {
        if (order < 1) throw new IllegalArgumentException("Order must be greater than 0");
        if (filePath == null || filePath.trim().isEmpty()) throw new IllegalArgumentException("File path cannot be empty");
        DiapasonsOfNumbersWithTotalQuantity diapasonsOfNumbersWithTotalQuantity = parseFile(filePath, order);
        if (diapasonsOfNumbersWithTotalQuantity.getTotalQuantity() < order) throw new IllegalArgumentException("Quantity of numbers  must be greater than order");
        int[] keyArray = diapasonsOfNumbersWithTotalQuantity.getDiapasonsWithNumbers()
                .keySet().stream().mapToInt(Integer::intValue).toArray();
        if (keyArray.length > 1) quickSortIterative(keyArray, 0, keyArray.length -1);
        List<Integer> numbers = new ArrayList<>();
        int arrayBound = (keyArray.length - 1 > 0) ? keyArray.length - 1 : 1;
        for (int i = 0; i < arrayBound; i++){
            Integer key = keyArray[i];
            numbers.addAll(diapasonsOfNumbersWithTotalQuantity.getDiapasonsWithNumbers().get(key));
            if (numbers.size() >= order) break;
        }
        int[] numbersToBeSorted = numbers.stream().mapToInt(Integer::intValue).toArray();
        if (numbersToBeSorted.length > 1) quickSortIterative(numbersToBeSorted, 0, numbersToBeSorted.length - 1);
        return String.valueOf(numbersToBeSorted[order - 1]);
    }

    private DiapasonsOfNumbersWithTotalQuantity parseFile(String filePath, Integer order) {
        if (!Files.exists(Path.of(filePath))) throw new IllegalArgumentException("File not found");
        Map<Integer, List<Integer>> diapasonsWithNumbers = new HashMap<>();
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
        } catch (Exception e) {
            throw new IllegalArgumentException("File parsing error");
        }
        return new DiapasonsOfNumbersWithTotalQuantity(diapasonsWithNumbers, totalNumbers);
    }

    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = (low - 1);
        for (int j = low; j <= high - 1; j++) {
            if (arr[j] <= pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }

    private void quickSortIterative(int[] arr, int l, int h) {
        int[] stack = new int[h - l + 1];
        int top = -1;
        stack[++top] = l;
        stack[++top] = h;
        while (top >= 0) {
            h = stack[top--];
            l = stack[top--];
            int p = partition(arr, l, h);
            if (p - 1 > l) {
                stack[++top] = l;
                stack[++top] = p - 1;
            }
            if (p + 1 < h) {
                stack[++top] = p + 1;
                stack[++top] = h;
            }
        }
    }

}
