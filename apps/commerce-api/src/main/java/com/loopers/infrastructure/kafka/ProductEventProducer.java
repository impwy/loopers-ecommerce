package com.loopers.infrastructure.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductEventProducer {
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public void send(String topic, String key, Object payload) {
        kafkaTemplate.send(topic, key, payload);
    }
}
