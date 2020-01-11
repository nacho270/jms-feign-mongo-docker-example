package com.nacho.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProduceController {

    private static final Logger log = LoggerFactory.getLogger(ProcessBuilder.class);

    @Autowired
    private ProducerService producerService;

    @PostMapping(value = "produce", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> produce(@RequestBody final String body) {
        log.info(body);
        producerService.produce(body);
        return ResponseEntity.ok().build();
    }
}
