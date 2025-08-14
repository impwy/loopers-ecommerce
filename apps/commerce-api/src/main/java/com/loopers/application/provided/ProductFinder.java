package com.loopers.application.provided;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.infrastructure.product.ProductQueryDslRepositoryImpl.ProductWithLikeCount;
import com.loopers.interfaces.api.order.dto.OrderV1Dto.Request.CreateOrderRequest;

public interface ProductFinder {
    Product find(Long productId);

    List<Product> findByConditions(Sort sort);

    List<Product> findByBrand(Brand brand);

    Page<ProductWithLikeCount> findWithLikeCount(String sortKey, Long brandId, Pageable pageable);

    BigDecimal getTotalPrice(List<CreateOrderRequest> orderRequests);

    Map<Long, Product> getProductMap(List<Long> productIds);
}
