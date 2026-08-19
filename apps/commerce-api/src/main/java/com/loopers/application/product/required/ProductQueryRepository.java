package com.loopers.application.product.required;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.loopers.application.product.ProductWithBrand;
import com.loopers.application.product.ProductWithLikeCount;

public interface ProductQueryRepository {
    Page<ProductWithLikeCount> findWithLikeCount(String sortKey, List<Long> brandIds, Pageable pageable);

    Page<ProductWithLikeCount> findByBrandDenormalizationWithLike(String sortKey, List<Long> brandIds,
                                                                  Pageable pageable);

    Page<ProductWithBrand> findByBrandDenormalization(String sortKey, List<Long> brandIds, Pageable pageable);
}
