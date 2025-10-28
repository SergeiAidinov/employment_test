package ru.yandex.incoming34.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.incoming34.dto.Query;
import ru.yandex.incoming34.service.Service;

@org.springframework.stereotype.Controller
public class Controller {

    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @PostMapping(value = "/load_file")
    public ResponseEntity<String> loadFile(@RequestBody Query query) {
        return ResponseEntity
                .ok()
                .header("Content-Type", "text/plain")
                .body(service.handleFile(query.getFilePath(), query.getOrder()));
    }
}
