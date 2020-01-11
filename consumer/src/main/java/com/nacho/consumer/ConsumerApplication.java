package com.nacho.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class ConsumerApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

}
