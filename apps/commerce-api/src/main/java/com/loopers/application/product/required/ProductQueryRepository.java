package com.loopers.application.product.required;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.loopers.domain.product.ProductInfo;

public interface ProductQueryRepository {
    Page<ProductInfo> findWithLikeCount(String sortKey, List<Long> brandIds, Pageable pageable);

    Page<ProductInfo> findByBrandDenormalizationWithLike(String sortKey, List<Long> brandIds, Pageable pageable);
}
