package com.loopers.application.provided;

import com.loopers.domain.like.ProductLike;

public interface ProductLikeFinder {
    ProductLike find(Long memberId, Long productId);

    void throwConflictExceptionHasLike(Long memberId, Long productId);
}
