package com.loopers.interfaces.consumer;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.loopers.application.provided.ProductMetricsRegister;
import com.loopers.confg.kafka.KafkaConfig;
import com.loopers.interfaces.consumer.dto.ProductPayload;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventConsumer {
    private final ProductMetricsRegister productMetricsRegister;

    @KafkaListener(
            topics = "product-event",
            groupId = "loopers-default-consumer",
            containerFactory = KafkaConfig.BATCH_LISTENER
    )
    public void productLikeListener(List<ProductPayload> messages,
                                    Acknowledgment acknowledgment) {
        productMetricsRegister.dailyUpdates(messages);
        acknowledgment.acknowledge();
    }
}
