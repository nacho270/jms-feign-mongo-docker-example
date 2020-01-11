package com.nacho.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class ObjectsConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectsConsumer.class);

    @Autowired
    private MongoService mongoService;

    @JmsListener(destination = "OBJECTS_QUEUE")
    public void listener(final Message<String> message) {
        final String object = message.getPayload();
        LOGGER.info("Message: " + object);
        mongoService.saveObject(object);

    }
}
