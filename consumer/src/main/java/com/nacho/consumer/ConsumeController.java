package com.nacho.consumer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumeController {

    @GetMapping("/ping")
    public ResponseEntity<String> produce() throws InterruptedException {
        Thread.sleep(200L);
        return ResponseEntity.ok("pong");
    }
}
