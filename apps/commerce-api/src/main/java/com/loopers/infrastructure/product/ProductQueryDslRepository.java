package com.loopers.infrastructure.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.loopers.infrastructure.product.ProductQueryDslRepositoryImpl.ProductWithLikeCount;

public interface ProductQueryDslRepository {
    Page<ProductWithLikeCount> findByBrandAndLikeCount(String sortKey, Long brandId, Pageable pageable);

    Page<ProductWithLikeCount> findByBrandAndLikeCountDenormalization(String sortKey, List<Long> brandId, Pageable pageable);
}
