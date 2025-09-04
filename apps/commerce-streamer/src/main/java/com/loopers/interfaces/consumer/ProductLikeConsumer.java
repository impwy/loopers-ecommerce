package com.loopers.interfaces.consumer;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.loopers.confg.kafka.KafkaConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductLikeConsumer {

    @KafkaListener(
            topics = "productLike",
            groupId = "loopers-default-consumer",
            containerFactory = KafkaConfig.BATCH_LISTENER
    )
    public void productLikeListener(List<ConsumerRecord<Object, Object>> messages,
                                    Acknowledgment acknowledgment) {
        messages.forEach(message -> {
            log.info("productLike: {}", message.value());
            acknowledgment.acknowledge();
        });
    }
}
