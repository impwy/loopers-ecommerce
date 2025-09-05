package com.loopers.domain.product;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "product_metrics")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductMetrics {

    @Id
    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "purchase_count")
    private Long purchaseCount;

    @Column(name = "published_at", nullable = false)
    private LocalDate publishedAt;

    private ProductMetrics(String eventId, Long productId, LocalDate publishedAt) {
        this.eventId = eventId;
        this.productId = productId;
        this.publishedAt = publishedAt;
    }

    public static ProductMetrics create(CreateProductMetricsSpec createProductMetricsSpec) {
        return new ProductMetrics(
                createProductMetricsSpec.eventId(),
                createProductMetricsSpec.productId(),
                createProductMetricsSpec.date()
        );
    }

    public void increaseLikes() {
        this.likeCount += 1L;
    }

    public void decreaseLikes() {
        this.likeCount -= 1L;
    }

    public void increaseSales(Long saleQuantity) {
        this.purchaseCount += saleQuantity;
    }

    public void decreaseSales(Long canceledQuantity) {
        this.purchaseCount += canceledQuantity;
    }

    public void increaseViews() {
        this.viewCount += 1L;
    }
}
