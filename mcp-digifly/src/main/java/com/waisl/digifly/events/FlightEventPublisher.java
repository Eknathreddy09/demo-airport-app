package com.waisl.digifly.events;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class FlightEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;

    public FlightEventPublisher(RabbitTemplate rabbitTemplate,
                                 @Value("${airport.events.exchange}") String exchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    public void publish(String routingKey, Map<String, Object> payload) {
        Map<String, Object> event = new java.util.LinkedHashMap<>(payload);
        event.put("source", "mcp-digifly");
        event.put("routingKey", routingKey);
        event.put("timestamp", Instant.now().toString());
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}
