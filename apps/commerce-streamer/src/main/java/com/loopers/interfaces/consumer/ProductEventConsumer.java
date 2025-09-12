package com.loopers.interfaces.consumer;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.loopers.application.provided.ProductMetricsRegister;
import com.loopers.application.ranking.ProductRankingFacade;
import com.loopers.confg.kafka.KafkaConfig;
import com.loopers.interfaces.consumer.dto.ProductPayload;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventConsumer {
    private final ProductMetricsRegister productMetricsRegister;
    private final ProductRankingFacade productRankingFacade;

    @KafkaListener(
            topics = "product-event",
            groupId = "product-consumer",
            containerFactory = KafkaConfig.BATCH_LISTENER
    )
    public void productListener(List<ProductPayload> messages,
                                Acknowledgment acknowledgment) {
        productMetricsRegister.handleProductEvent(messages);
        productRankingFacade.rank(messages);
        acknowledgment.acknowledge();
    }
}
