package com.loopers.domain.product;

import java.math.BigDecimal;

import com.loopers.domain.brand.Brand;

public record ProductInfo(Long productId, Long brandId, String productName, String productDescription, BigDecimal price,
                          String brandName, String brandDescription, Long likeCount) {
    public static ProductInfo of(Product product, Brand brand, Long likeCount) {
        return new ProductInfo(product.getId(), brand.getId(), product.getName(), product.getDescription(), product.getPrice(),
                               brand.getName(), brand.getDescription(), likeCount);
    }
}
