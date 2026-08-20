package com.loopers.application.product.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.loopers.domain.product.ProductPayload.ProductEventType;
import com.loopers.domain.product.outbox.CreateProductOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox;
import com.loopers.support.stereotype.ApplicationValidServiceTest;
import com.loopers.utils.DatabaseCleanUp;

import lombok.RequiredArgsConstructor;

@ApplicationValidServiceTest
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class ProductOutboxRegisterTest {
    final ProductOutboxRegister productOutboxRegister;

    final DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("상품 이벤트 outbox 생성 통합 테스트")
    @Test
    void create_product_event_outbox_test() {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        CreateProductOutbox createProductOutbox = new CreateProductOutbox(1L, uuidString, ProductEventType.PRODUCT_LIKE_INCREMENT, 0L, ZonedDateTime.now());

        ProductEventOutbox expected = productOutboxRegister.register(createProductOutbox);

        assertAll(
                () -> assertThat(expected.getEventId()).isEqualTo(createProductOutbox.eventId()),
                () -> assertThat(expected.getEventType()).isEqualTo(createProductOutbox.eventType())
        );
    }


    // outbox와 이벤트 publish 테스트를 분리해야겠다.
    /*@DisplayName("좋아요 감소 시 감소 이벤트 타입으로 outbox 를 생성한다")
    @Test
    void decrease_like_count_creates_decrement_outbox() {
        Long productId = 1L;
        ProductEventOutbox outbox = ProductEventOutbox.create(productId, "event-id",
                                                              ProductEventType.PRODUCT_LIKE_DECREMENT, 0L, null);
        when(productOutboxRegister.register(any(CreateProductOutbox.class))).thenReturn(outbox);
        ArgumentCaptor<CreateProductOutbox> outboxCaptor = ArgumentCaptor.forClass(CreateProductOutbox.class);
        ArgumentCaptor<LikeDecrease> eventCaptor = ArgumentCaptor.forClass(LikeDecrease.class);

        productFacade.decreaseLikeCount(productId);

        verify(productRegister).decreaseLike(productId);
        verify(productOutboxRegister).register(outboxCaptor.capture());
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(outboxCaptor.getValue().eventType()).isEqualTo(ProductEventType.PRODUCT_LIKE_DECREMENT);
        assertThat(eventCaptor.getValue().productId()).isEqualTo(productId);
    }

    @DisplayName("좋아요 증가 시 증가 이벤트 타입으로 outbox 를 생성한다")
    @Test
    void increase_like_count_creates_increment_outbox() {
        Long productId = 1L;
        ProductEventOutbox outbox = ProductEventOutbox.create(productId, "event-id",
                                                              ProductEventType.PRODUCT_LIKE_INCREMENT, 0L, null);
        when(productOutboxRegister.register(any(CreateProductOutbox.class))).thenReturn(outbox);
        ArgumentCaptor<CreateProductOutbox> outboxCaptor = ArgumentCaptor.forClass(CreateProductOutbox.class);
        ArgumentCaptor<LikeIncrease> eventCaptor = ArgumentCaptor.forClass(LikeIncrease.class);

        productFacade.increaseLikeCount(productId);

        verify(productRegister).increaseLike(productId);
        verify(productOutboxRegister).register(outboxCaptor.capture());
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(outboxCaptor.getValue().eventType()).isEqualTo(ProductEventType.PRODUCT_LIKE_INCREMENT);
        assertThat(eventCaptor.getValue().productId()).isEqualTo(productId);
    }*/
}
