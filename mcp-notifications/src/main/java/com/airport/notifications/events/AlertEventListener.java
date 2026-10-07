package com.airport.notifications.events;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AlertEventListener {

    private final AlertBuffer buffer;

    public AlertEventListener(AlertBuffer buffer) {
        this.buffer = buffer;
    }

    @RabbitListener(queues = "${airport.events.queue}")
    public void onEvent(Map<String, Object> event) {
        buffer.add(event);
    }
}
