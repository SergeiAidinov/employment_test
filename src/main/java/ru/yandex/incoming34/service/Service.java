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
        DiapasonsOfNumbersWithTotalQuantity diapasonsOfNumbersWithTotalQuantity = parseFile(filePath, order);
        if (diapasonsOfNumbersWithTotalQuantity.getTotalQuantity() < order) throw new IllegalArgumentException("Quantity of numbers  must be greater than order");
        int[] keyArray = diapasonsOfNumbersWithTotalQuantity.getDiapasonsWithNumbers()
                .keySet().stream().mapToInt(Integer::intValue).toArray();
        quickSortIterative(keyArray, 0, keyArray.length -1);
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < keyArray.length - 1; i++){
            Integer key = keyArray[i];
            numbers.addAll(diapasonsOfNumbersWithTotalQuantity.getDiapasonsWithNumbers().get(key));
            if (numbers.size() >= order) break;
        }
        int[] nubersToBeSorted = numbers.stream().mapToInt(Integer::intValue).toArray();
        quickSortIterative(nubersToBeSorted, 0, nubersToBeSorted.length - 1);
        return String.valueOf(nubersToBeSorted[order - 1]);
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

    int partition(int arr[], int low, int high)
    {
        int pivot = arr[high];

        // index of smaller element
        int i = (low - 1);
        for (int j = low; j <= high - 1; j++) {
            // If current element is smaller than or
            // equal to pivot
            if (arr[j] <= pivot) {
                i++;

                // swap arr[i] and arr[j]
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        // swap arr[i+1] and arr[high] (or pivot)
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }

    /* A[] --> Array to be sorted,
   l  --> Starting index,
   h  --> Ending index */
    void quickSortIterative(int arr[], int l, int h)
    {
        // Create an auxiliary stack
        int[] stack = new int[h - l + 1];

        // initialize top of stack
        int top = -1;

        // push initial values of l and h to stack
        stack[++top] = l;
        stack[++top] = h;

        // Keep popping from stack while is not empty
        while (top >= 0) {
            // Pop h and l
            h = stack[top--];
            l = stack[top--];

            // Set pivot element at its correct position
            // in sorted array
            int p = partition(arr, l, h);

            // If there are elements on left side of pivot,
            // then push left side to stack
            if (p - 1 > l) {
                stack[++top] = l;
                stack[++top] = p - 1;
            }

            // If there are elements on right side of pivot,
            // then push right side to stack
            if (p + 1 < h) {
                stack[++top] = p + 1;
                stack[++top] = h;
            }
        }
    }

}
