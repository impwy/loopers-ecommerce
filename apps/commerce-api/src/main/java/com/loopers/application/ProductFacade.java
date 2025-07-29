package com.loopers.application;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.application.provided.BrandFinder;
import com.loopers.application.provided.ProductFinder;
import com.loopers.application.provided.ProductLikeFinder;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductFinder productFinder;
    private final BrandFinder brandFinder;
    private final ProductLikeFinder productLikeFinder;

    @Transactional
    public ProductInfo findProductInfo(Long productId) {
        Product product = productFinder.find(productId);
        Brand brand = brandFinder.find(product.getBrand().getId());
        Long likeCount = productLikeFinder.countByProductId(productId);

        return ProductInfo.of(product, brand, likeCount);
    }
}
