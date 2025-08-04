package com.loopers.domain.product;

import org.springframework.stereotype.Component;

import com.loopers.domain.brand.Brand;

@Component
public class ProductBrandDomainService {

    public ProductInfo findProductWithBrand(Product product, Brand brand, Long likeCount) {
        return ProductInfo.of(product, brand, likeCount);
    }
}
