package com.loopers.application.provided;

import com.loopers.domain.product.Product;

public interface ProductFinder {
    Product find(Long productId);
}
