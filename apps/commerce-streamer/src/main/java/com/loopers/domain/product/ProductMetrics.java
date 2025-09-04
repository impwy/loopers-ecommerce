package com.loopers.domain.product;

import java.time.LocalDate;

import com.loopers.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "product_metrics")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductMetrics extends BaseEntity {

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "purchase_count")
    private Long purchaseCount;

    @Column(name = "published_at")
    private LocalDate publishedAt;

    private ProductMetrics(Long productId, LocalDate publishedAt) {
        this.productId = productId;
        this.publishedAt = publishedAt;
    }

    public static ProductMetrics create(CreateProductMetricsSpec createProductMetricsSpec) {
        return new ProductMetrics(
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

    public void increaseSales() {
        this.purchaseCount += 1L;
    }

    public void increaseViews() {
        this.viewCount += 1L;
    }
}
