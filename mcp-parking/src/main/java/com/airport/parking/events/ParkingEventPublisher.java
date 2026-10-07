package com.airport.parking.events;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ParkingEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;

    public ParkingEventPublisher(RabbitTemplate rabbitTemplate,
                                  @Value("${airport.events.exchange}") String exchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    public void publish(String routingKey, Map<String, Object> payload) {
        Map<String, Object> event = new LinkedHashMap<>(payload);
        event.put("source", "mcp-parking");
        event.put("routingKey", routingKey);
        event.put("timestamp", Instant.now().toString());
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}
