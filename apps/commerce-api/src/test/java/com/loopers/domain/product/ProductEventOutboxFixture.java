package com.loopers.domain.product;

import java.time.ZonedDateTime;

import com.loopers.domain.product.ProductPayload.ProductEventType;
import com.loopers.domain.product.outbox.CreateProductOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox;

import org.instancio.Instancio;

import static org.instancio.Select.field;

public final class ProductEventOutboxFixture {
    private static final Long DEFAULT_PRODUCT_ID = 1L;

    private ProductEventOutboxFixture() {
    }

    public static ProductEventOutbox createProductEventOutbox() {
        return Instancio.of(ProductEventOutbox.class)
                        .ignore(field(ProductEventOutbox::getId))
                        .set(field(ProductEventOutbox::getProductId), DEFAULT_PRODUCT_ID)
                        .generate(field(ProductEventOutbox::getEventId), gen -> gen.text().uuid())
                        .generate(field(ProductEventOutbox::getEventType), gen -> gen.enumOf(ProductEventType.class))
                        .generate(field(ProductEventOutbox::getVersion), gen -> gen.longs().range(0L, 10L))
                        .set(field(ProductEventOutbox::getProductOutboxStatus),
                             ProductEventOutbox.ProductOutboxStatus.PENDING)
                        .set(field(ProductEventOutbox::getPayload), null)
                        .set(field(ProductEventOutbox::getPublishedAt), ZonedDateTime.now())
                        .create();
    }

    public static ProductEventOutbox createProductEventOutbox(Long productId, String eventId,
                                                              ProductEventType eventType, Long version,
                                                              ZonedDateTime publishedAt) {
        return Instancio.of(ProductEventOutbox.class)
                        .ignore(field(ProductEventOutbox::getId))
                        .set(field(ProductEventOutbox::getProductId), productId)
                        .set(field(ProductEventOutbox::getEventId), eventId)
                        .set(field(ProductEventOutbox::getEventType), eventType)
                        .set(field(ProductEventOutbox::getVersion), version)
                        .set(field(ProductEventOutbox::getProductOutboxStatus),
                             ProductEventOutbox.ProductOutboxStatus.PENDING)
                        .set(field(ProductEventOutbox::getPayload), null)
                        .set(field(ProductEventOutbox::getPublishedAt), publishedAt)
                        .create();
    }

    public static CreateProductOutbox createProductOutbox() {
        return Instancio.of(CreateProductOutbox.class)
                        .set(field(CreateProductOutbox::productId), DEFAULT_PRODUCT_ID)
                        .generate(field(CreateProductOutbox::eventId), gen -> gen.text().uuid())
                        .generate(field(CreateProductOutbox::eventType), gen -> gen.enumOf(ProductEventType.class))
                        .generate(field(CreateProductOutbox::version), gen -> gen.longs().range(0L, 10L))
                        .set(field(CreateProductOutbox::publishedAt), ZonedDateTime.now())
                        .create();
    }

    public static CreateProductOutbox createProductOutbox(Long productId, String eventId,
                                                          ProductEventType eventType, Long version,
                                                          ZonedDateTime publishedAt) {
        return Instancio.of(CreateProductOutbox.class)
                        .set(field(CreateProductOutbox::productId), productId)
                        .set(field(CreateProductOutbox::eventId), eventId)
                        .set(field(CreateProductOutbox::eventType), eventType)
                        .set(field(CreateProductOutbox::version), version)
                        .set(field(CreateProductOutbox::publishedAt), publishedAt)
                        .create();
    }
}
