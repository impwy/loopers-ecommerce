package com.loopers.domain.ranking;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "mv_product_rank_monthly")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MvProductRankMonthly {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "rank", nullable = false)
    private Integer rank;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt = LocalDate.now();

    private MvProductRankMonthly(Long productId, Double score, Integer rank, LocalDate startDate, LocalDate endDate) {
        this.productId = productId;
        this.score = score;
        this.rank = rank;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static MvProductRankMonthly create(Long productId, double score, int rank, LocalDate startDate, LocalDate endDate) {
        return new MvProductRankMonthly(productId, score, rank, startDate, endDate);
    }
}
