package com.loopers.application.provided;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.infrastructure.product.ProductQueryDslRepositoryImpl.ProductWithLikeCount;

public interface ProductFinder {
    Product find(Long productId);

    List<Product> findByConditions(Sort sort);

    List<Product> findByBrand(Brand brand);

    Page<ProductWithLikeCount> findWithLikeCount(String sortKey, Pageable pageable);
}
