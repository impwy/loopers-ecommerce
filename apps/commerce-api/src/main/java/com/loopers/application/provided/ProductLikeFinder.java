package com.loopers.application.provided;

import java.util.Optional;

import com.loopers.domain.like.ProductLike;

public interface ProductLikeFinder {
    ProductLike find(Long memberId, Long productId);

    Optional<ProductLike> restoreDeletedOrThrowConflict(Long memberId, Long productId);

    Long countByProductId(Long productId);
}
