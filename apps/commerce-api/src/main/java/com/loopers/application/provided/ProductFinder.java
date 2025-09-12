package com.loopers.application.provided;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.loopers.application.product.ProductTotalAmountRequest;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;
import com.loopers.infrastructure.product.ProductWithLikeCount;
import com.loopers.interfaces.api.product.dto.ProductV1Dto.Response.ProductInfoPageResponse;

public interface ProductFinder {
    Product find(Long productId);

    ProductInfo findCachedProduct(Long productId);

    List<Product> findByConditions(Sort sort);

    List<Product> findByBrand(Brand brand);

    Page<ProductWithLikeCount> findWithLikeCount(String sortKey, List<Long> brandIds, Pageable pageable);

    Page<ProductWithLikeCount> findByBrandAndLikeCountDenormalization(String sortKey, List<Long> brandId, Pageable pageable);

    Page<ProductWithLikeCount> findByBrandAndLikeCountDenormalizationWithRedis(String sortKey, List<Long> brandId,
                                                                               Pageable pageable);

    BigDecimal getTotalPrice(List<ProductTotalAmountRequest> productTotalAmountRequests);

    Map<Long, Product> getProductMap(List<Long> productIds);

    Product findProductPessimisticLock(Long productId);

    ProductInfoPageResponse findProductInfoWithRank(String date, Pageable pageable);
}
