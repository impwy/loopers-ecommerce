package com.loopers.application.product;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;

public record ProductWithLikeCount(Product product, Brand brand, Long likeCount) {
}
