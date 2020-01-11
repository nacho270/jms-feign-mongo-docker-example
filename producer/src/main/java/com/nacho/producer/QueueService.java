package com.nacho.producer;

import javax.jms.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

    @Autowired
    private Queue queue;

    @Autowired
    private JmsTemplate jmsTemplate;

    public void notifyObject(final String object) {
        jmsTemplate.convertAndSend(queue, object);
    }

}
