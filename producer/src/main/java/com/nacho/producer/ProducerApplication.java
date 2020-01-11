package com.nacho.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@EnableFeignClients
@SpringBootApplication
public class ProducerApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

}
