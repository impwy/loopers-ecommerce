package com.loopers.application;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.application.provided.ProductFinder;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductFinder productFinder;
    private final ProductService productService;

    @Transactional
    public ProductInfo findProductInfo(Long productId) {
        Product product = productFinder.find(productId);
        return productService.findProductWithBrand(product);
    }
}
