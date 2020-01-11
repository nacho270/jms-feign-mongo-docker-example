package com.nacho.producer;

import javax.jms.Queue;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Contract;

@Configuration
public class ProducerConfig {

    @Bean
    public Queue queue() {
        return new ActiveMQQueue("OBJECTS_QUEUE");
    }

    @Bean
    public Contract feignContract() {
        return new feign.Contract.Default();
    }
}
