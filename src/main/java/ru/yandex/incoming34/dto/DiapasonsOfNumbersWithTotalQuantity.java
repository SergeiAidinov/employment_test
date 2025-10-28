package ru.yandex.incoming34.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class DiapasonsOfNumbersWithTotalQuantity {
    Map<Integer, List<Integer>> diapasonsWithNumbers;
    int totalQuantity;
}
