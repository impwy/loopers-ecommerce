package com.loopers.domain.product;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.ProductLikeFinder;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductService {
    private final ProductLikeFinder productLikeFinder;

    public ProductInfo findProductWithBrand(Product product) {
        Long likeCount = productLikeFinder.countByProductId(product.getId());
        return ProductInfo.of(product, likeCount);
    }
}
