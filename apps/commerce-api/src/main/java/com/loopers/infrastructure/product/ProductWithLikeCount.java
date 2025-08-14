package com.loopers.infrastructure.product;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;

public record ProductWithLikeCount(Product product, Brand brand, Long likeCount) {
}
