package com.loopers.domain.product;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import com.loopers.domain.brand.Brand;

public record CreateProductSpec(String name, String description, BigDecimal price, Brand brand, ZonedDateTime latestAt) {
    public Product toEntity() {
        return Product.create(name, description, price, brand, latestAt);
    }
}
