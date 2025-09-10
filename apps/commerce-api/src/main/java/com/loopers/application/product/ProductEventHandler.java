package com.loopers.application.product;

import java.time.ZonedDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.application.provided.ProductOutboxFinder;
import com.loopers.application.provided.ProductOutboxRegister;
import com.loopers.domain.product.LikeDecrease;
import com.loopers.domain.product.LikeIncrease;
import com.loopers.domain.product.outbox.ProductEventOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox.ProductOutboxStatus;
import com.loopers.domain.product.ProductPayload;
import com.loopers.domain.product.ProductPayload.ProductEventType;
import com.loopers.infrastructure.kafka.ProductEventProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventHandler {
    private final ProductOutboxRegister productOutboxRegister;
    private final ProductOutboxFinder productOutboxFinder;
    private final ProductEventProducer productEventProducer;
    private final ObjectMapper objectMapper;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handler(LikeIncrease event) {
        Long outboxId = event.outboxId();
        ProductEventOutbox productEventOutbox = productOutboxFinder.find(outboxId);

        Long productId = event.productId();
        ProductPayload payload = new ProductPayload(productId, productEventOutbox.getEventId(),
                                                    ProductEventType.PRODUCT_LIKE_INCREMENT,
                                                    0L, ZonedDateTime.now(), null, null, null);

        updateOutbox(outboxId, payload);

        publishProductLikeEvent(payload, productEventOutbox, productId);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handler(LikeDecrease event) {
        Long outboxId = event.outboxId();
        ProductEventOutbox productEventOutbox = productOutboxFinder.find(outboxId);

        Long productId = event.productId();
        ProductPayload payload = new ProductPayload(productId, productEventOutbox.getEventId(),
                                                    ProductEventType.PRODUCT_LIKE_DECREMENT,
                                                    0L, ZonedDateTime.now(), null, null, null);

        updateOutbox(outboxId, payload);

        publishProductLikeEvent(payload, productEventOutbox, productId);
    }

    private void updateOutbox(Long outboxId, ProductPayload payload) {
        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            productOutboxRegister.updatePayload(outboxId, payloadJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void publishProductLikeEvent(ProductPayload payload, ProductEventOutbox productEventOutbox, Long productId) {
        try {
            productEventProducer.send("product-event", String.valueOf(productId), payload);
            productOutboxRegister.changeStatus(productEventOutbox.getId(), ProductOutboxStatus.COMPLETED);
        } catch (Exception e) {
            productOutboxRegister.changeStatus(productEventOutbox.getId(), ProductOutboxStatus.FAILED);
            log.warn("상품 좋아요 이벤트 전송 실패 eventId:{}, productId:{}", productEventOutbox.getEventId(), productId);
        }
    }
}
