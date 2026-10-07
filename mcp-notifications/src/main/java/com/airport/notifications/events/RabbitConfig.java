package com.airport.notifications.events;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange airportEventsExchange(@Value("${airport.events.exchange}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue notificationsQueue(@Value("${airport.events.queue}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding notificationsBinding(Queue notificationsQueue, TopicExchange airportEventsExchange) {
        return BindingBuilder.bind(notificationsQueue).to(airportEventsExchange).with("#");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
