package com.loopers.application;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.ProductFinder;
import com.loopers.application.required.ProductRepository;
import com.loopers.domain.product.Product;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductQueryService implements ProductFinder {
    private final ProductRepository productRepository;

    @Override
    public Product find(Long productId) {
        Product product = productRepository.find(productId);
        return product;
    }
}
