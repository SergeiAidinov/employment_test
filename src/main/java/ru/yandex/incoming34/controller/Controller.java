package ru.yandex.incoming34.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.incoming34.dto.Query;
import ru.yandex.incoming34.service.Service;

@RestController
public class Controller {

    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @PostMapping(value = "/load_file")
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> loadFile(@RequestBody Query query) {
        ResponseEntity<String> response;
        try{
            response = ResponseEntity
                    .ok()
                    .header("Content-Type", "text/plain")
                    .body(service.handleFile(query.getFilePath(), query.getOrder()));
        } catch (IllegalArgumentException ex){
            return ResponseEntity
            .ok()
            .body(ex.getMessage());
        }
        return response;
    }
}
