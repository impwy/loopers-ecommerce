package com.loopers.application.required;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.infrastructure.product.ProductQueryDslRepositoryImpl.ProductWithLikeCount;

public interface ProductRepository {
    Product save(Product product);

    Optional<Product> find(Long productId);

    List<Product> findByConditions(Sort sort);

    List<Product> findByBrand(Brand brand);

    Page<ProductWithLikeCount> findWithLikeCount(String sortKey, Pageable pageable);
}
