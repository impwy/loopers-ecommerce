package com.loopers.application;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.application.provided.ProductFinder;
import com.loopers.application.provided.ProductLikeFinder;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductBrandDomainService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductFinder productFinder;
    private final ProductLikeFinder productLikeFinder;
    private final ProductBrandDomainService productBrandDomainService = new ProductBrandDomainService();

    @Transactional
    public ProductInfo findProductInfo(Long productId) {
        Product product = productFinder.find(productId);
        Brand brand = product.getBrand();
        Long likeCount = productLikeFinder.countByProductId(productId);
        return productBrandDomainService.findProductWithBrand(product, brand, likeCount);
    }
}
