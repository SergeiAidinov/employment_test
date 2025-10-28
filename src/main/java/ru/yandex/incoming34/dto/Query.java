package ru.yandex.incoming34.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Query {
    private String filePath;
    private Integer order;
}
