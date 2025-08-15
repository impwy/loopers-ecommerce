package com.loopers.infrastructure.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQueryDslRepository {
    Page<ProductWithLikeCount> findByBrandAndLikeCount(String sortKey, List<Long> brandIds, Pageable pageable);

    Page<ProductWithLikeCount> findByBrandNormalization(String sortKey, List<Long> brandIds, Pageable pageable);

    Page<ProductWithBrand> findByBrandDenormalization(String sortKey, List<Long> brandIds, Pageable pageable);
}
