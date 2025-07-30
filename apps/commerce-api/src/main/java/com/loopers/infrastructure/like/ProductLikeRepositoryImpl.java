package com.loopers.infrastructure.like;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.loopers.application.required.ProductLikeRepository;
import com.loopers.domain.like.ProductLike;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductLikeRepositoryImpl implements ProductLikeRepository {
    private final ProductLikeJpaRepository productLikeJpaRepository;

    @Override
    public ProductLike save(ProductLike productLike) {
        return productLikeJpaRepository.save(productLike);
    }

    @Override
    public Optional<ProductLike> findByMemberIdAndProductId(Long memberId, Long productId) {
        return productLikeJpaRepository.findByMemberIdAndProductId(memberId, productId);
    }

    @Override
    public Long countByProductId(Long productId) {
        return productLikeJpaRepository.countByProductId(productId);
    }
}
