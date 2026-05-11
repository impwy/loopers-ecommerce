package com.loopers.application.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.loopers.application.provided.ProductFinder;
import com.loopers.application.provided.ProductOutboxRegister;
import com.loopers.application.provided.ProductRegister;
import com.loopers.domain.product.LikeIncrease;
import com.loopers.domain.product.LikeDecrease;
import com.loopers.domain.product.ProductBrandDomainService;
import com.loopers.domain.product.ProductPayload.ProductEventType;
import com.loopers.domain.product.outbox.CreateProductOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox;

@ExtendWith(MockitoExtension.class)
class ProductFacadeTest {
    @Mock
    private ProductFinder productFinder;

    @Mock
    private ProductBrandDomainService productBrandDomainService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ProductRegister productRegister;

    @Mock
    private ProductOutboxRegister productOutboxRegister;

    @InjectMocks
    private ProductFacade productFacade;

    @DisplayName("좋아요 감소 시 감소 이벤트 타입으로 outbox 를 생성한다")
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
    }
}
