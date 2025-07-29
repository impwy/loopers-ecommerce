package com.loopers.application.required;

import com.loopers.domain.like.ProductLike;

public interface ProductLikeRepository {
    ProductLike save(ProductLike productLike);
}
