package com.loopers.domain.product.outbox;

import java.time.ZonedDateTime;

import com.loopers.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "product_event_outbox")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductEventOutbox extends BaseEntity {
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "event_id", nullable = false, unique = true)
    private String eventId;

    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductOutboxEventType eventType;

    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductOutboxStatus productOutboxStatus = ProductOutboxStatus.PENDING;

    @Column(name = "published_at")
    private ZonedDateTime publishedAt;

    public enum ProductOutboxStatus {
        PENDING,
        COMPLETED,
        FAILED
    }

    public enum ProductOutboxEventType {
        PRODUCT_LIKE_INCREMENT,
        PRODUCT_LIKE_DECREMENT
    }

    private ProductEventOutbox(Long productId, String eventId, ProductOutboxEventType eventType, Long version,
                               ZonedDateTime publishedAt) {
        this.productId = productId;
        this.eventId = eventId;
        this.eventType = eventType;
        this.version = version;
        this.publishedAt = publishedAt;
    }

    public static ProductEventOutbox create(Long productId, String eventId, ProductOutboxEventType eventType, Long version,
                                            ZonedDateTime publishedAt) {
        return new ProductEventOutbox(productId, eventId, eventType, version, publishedAt);
    }

    public static ProductEventOutbox create(CreateProductOutbox createProductOutbox) {
        return new ProductEventOutbox(
                createProductOutbox.productId(),
                createProductOutbox.eventId(),
                createProductOutbox.eventType(),
                createProductOutbox.version(),
                createProductOutbox.publishedAt()
        );
    }


    public void changeStatus(ProductOutboxStatus productOutboxStatus) {
        this.productOutboxStatus = productOutboxStatus;
    }
}
