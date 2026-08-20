package com.loopers.application.product.required;

public interface ProductEventPublisher {
    void send(String topic, String key, Object payload);
}
