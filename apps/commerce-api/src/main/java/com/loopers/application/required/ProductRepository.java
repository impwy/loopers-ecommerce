package com.loopers.application.required;

import com.loopers.domain.product.Product;

public interface ProductRepository {
    Product save(Product product);

    Product find(Long productId);
}
