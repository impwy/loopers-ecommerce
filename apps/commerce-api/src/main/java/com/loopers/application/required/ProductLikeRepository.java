package com.loopers.application.required;

import java.util.Optional;

import com.loopers.domain.like.ProductLike;

public interface ProductLikeRepository {
    ProductLike save(ProductLike productLike);

    Optional<ProductLike> findByMemberIdAndProductId(Long memberId, Long productId);

    Long countByProductId(Long productId);
}
