package com.nacho.producer;

import org.springframework.cloud.openfeign.FeignClient;

import feign.RequestLine;

@FeignClient(name = "consumer", url = "${consumer.url}")
public interface ConsumerRestAPI {

    @RequestLine("GET /ping")
    String doPing();
}
