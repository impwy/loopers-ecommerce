package com.loopers.domain.ranking;

import java.time.LocalDate;

import com.loopers.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "mv_product_rank_daily")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MvProductRankDaily extends BaseEntity {
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "date")
    private LocalDate issuedDate;

    @Column(name = "score")
    private Double score;

    @Column(name = "rank")
    private Integer rank;

    private MvProductRankDaily(Long productId, LocalDate issuedDate, Double score, Integer rank) {
        this.productId = productId;
        this.issuedDate = issuedDate;
        this.score = score;
        this.rank = rank;
    }

    public static MvProductRankDaily create(Long productId, LocalDate issuedDate, Double score, Integer rank) {
        return new MvProductRankDaily(productId, issuedDate, score, rank);
    }
}
