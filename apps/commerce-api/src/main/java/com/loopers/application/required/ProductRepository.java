package com.loopers.application.required;

import java.util.List;

import org.springframework.data.domain.Sort;

import com.loopers.domain.product.Product;

public interface ProductRepository {
    Product save(Product product);

    Product find(Long productId);

    List<Product> findByConditions(Sort sort);
}
