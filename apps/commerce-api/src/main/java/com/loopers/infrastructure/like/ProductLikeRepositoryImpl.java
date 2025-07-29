package com.loopers.infrastructure.like;

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
}
