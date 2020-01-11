package com.nacho.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.MongoClient;

@Configuration
public class ConsumerConfig {

    @Value(value = "${mongoHost}")
    private String mongoHost;

    @Value(value = "${mongoPort}")
    private String mongoPort;

    @Bean
    public MongoClient mongoClient() {
        return new MongoClient(mongoHost, Integer.valueOf(mongoPort));
    }

}
