package com.loopers.domain.product;

public record ProductInfoWithRank(ProductInfo productInfo, Long rank) {

    public static ProductInfoWithRank of(ProductInfo productInfo, Long rank) {
        return new ProductInfoWithRank(productInfo, rank);
    }
}
