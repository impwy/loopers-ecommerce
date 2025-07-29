package com.loopers.domain.product;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import com.loopers.domain.brand.Brand;

public record ProductInfo(Long productId, Long brandId, String productName, String productDescription, BigDecimal price,
                          String brandName, String brandDescription, ZonedDateTime productCreatedAt,
                          ZonedDateTime productUpdatedAt, Long likeCount) {

    public static ProductInfo of(Product product, Long likeCount) {
        Brand brand = product.getBrand();

        return new ProductInfo(product.getId(), brand.getId(), product.getName(), product.getDescription(), product.getPrice(),
                               brand.getName(), brand.getDescription(), product.getCreatedAt(), product.getUpdatedAt(), likeCount);
    }
}
