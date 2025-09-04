package com.loopers.domain.product.outbox;

import java.time.ZonedDateTime;

import com.loopers.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "product_event_outbox")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductEventOutbox extends BaseEntity {
    @Column(name = "event_id", nullable = false, unique = true)
    private String eventId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "version", nullable = false)
    private Long version;

    @Lob
    @Column(name = "payload", nullable = false)
    private String payload;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Column(name = "published_at")
    private ZonedDateTime publishedAt;

    public enum Status {
        PENDING,
        PROCESSED
    }

    private ProductEventOutbox(String eventId, String eventType, Long version, String payload, ZonedDateTime publishedAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.version = version;
        this.payload = payload;
        this.publishedAt = publishedAt;
    }

    public static ProductEventOutbox create(String eventId, String eventType, Long version,
                                            String payload, ZonedDateTime publishedAt) {
        return new ProductEventOutbox(eventId, eventType, version, payload, publishedAt);
    }

    public static ProductEventOutbox create(CreateProductOutbox createProductOutbox) {
        return new ProductEventOutbox(
                createProductOutbox.eventId(),
                createProductOutbox.eventType(),
                createProductOutbox.version(),
                createProductOutbox.payload(),
                createProductOutbox.publishedAt()
        );
    }
}
