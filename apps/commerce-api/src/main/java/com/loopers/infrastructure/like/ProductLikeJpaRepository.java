package com.loopers.infrastructure.like;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.like.ProductLike;

public interface ProductLikeJpaRepository extends JpaRepository<ProductLike, Long> {
}
