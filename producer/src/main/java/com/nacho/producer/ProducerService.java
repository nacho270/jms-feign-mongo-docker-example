package com.nacho.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {

    private static final Logger log = LoggerFactory.getLogger(ProducerService.class);

    @Autowired
    private QueueService queueService;

    @Autowired
    private ConsumerRestAPI consumerApi;

    public void produce(final String object) {
        log.info("Doing api call...");
        log.info("API RESPONSE: " + consumerApi.doPing());
        queueService.notifyObject(object);
    }
}
