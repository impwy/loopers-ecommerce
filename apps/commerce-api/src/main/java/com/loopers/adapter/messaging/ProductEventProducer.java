package com.loopers.adapter.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.loopers.application.product.required.ProductEventPublisher;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductEventProducer implements ProductEventPublisher {
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void send(String topic, String key, Object payload) {
        kafkaTemplate.send(topic, key, payload);
    }
}
