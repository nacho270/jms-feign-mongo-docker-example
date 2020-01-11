package com.nacho.consumer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumeController {

    @Autowired
    private MongoService mongoService;

    @GetMapping("/ping")
    public ResponseEntity<String> produce() throws InterruptedException {
        Thread.sleep(200L);
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> list() {
        return ResponseEntity.ok(mongoService.list());
    }
}
