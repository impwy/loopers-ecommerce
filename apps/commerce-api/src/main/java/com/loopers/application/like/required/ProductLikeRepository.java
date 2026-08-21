package com.loopers.application.like.required;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.loopers.domain.like.ProductLike;

public interface ProductLikeRepository extends Repository<ProductLike, Long> {
    ProductLike save(ProductLike productLike);

    Optional<ProductLike> findByMemberIdAndProductId(Long memberId, Long productId);

    Long countByProductId(Long productId);
}
