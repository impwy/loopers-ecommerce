package com.loopers.application.product.required;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

import com.loopers.domain.product.ProductEventOutboxFixture;
import com.loopers.domain.product.ProductPayload.ProductEventType;
import com.loopers.domain.product.outbox.ProductEventOutbox;
import com.loopers.support.BaseRepositoryTest;
import com.loopers.support.stereotype.ApplicationJpaServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationJpaServiceTest
@RequiredArgsConstructor
class ProductEventOutboxRepositoryTest extends BaseRepositoryTest {
    private final ProductEventOutboxRepository productEventOutboxRepository;

    @Test
    void saveAndFindById() {
        ProductEventOutbox outbox = productEventOutboxRepository.save(createOutbox(1L));
        flushAndClear();

        ProductEventOutbox found = productEventOutboxRepository.findById(outbox.getId()).orElseThrow();

        assertAll(
                () -> assertThat(found.getProductId()).isEqualTo(1L),
                () -> assertThat(found.getEventId()).isEqualTo(outbox.getEventId()),
                () -> assertThat(found.getEventType()).isEqualTo(ProductEventType.PRODUCT_LIKE_INCREMENT)
        );
    }

    @Test
    void findByProductIdAndEventIdAndEventType() {
        ProductEventOutbox outbox = productEventOutboxRepository.save(createOutbox(2L));
        flushAndClear();

        ProductEventOutbox found = productEventOutboxRepository
                .findByProductIdAndEventIdAndEventType(outbox.getProductId(), outbox.getEventId(), outbox.getEventType())
                .orElseThrow();

        assertThat(found.getId()).isEqualTo(outbox.getId());
    }

    private ProductEventOutbox createOutbox(Long productId) {
        return ProductEventOutboxFixture.createProductEventOutbox(
                productId, "event-" + productId, ProductEventType.PRODUCT_LIKE_INCREMENT,
                0L, ProductEventOutboxFixture.createProductOutbox().publishedAt());
    }
}
