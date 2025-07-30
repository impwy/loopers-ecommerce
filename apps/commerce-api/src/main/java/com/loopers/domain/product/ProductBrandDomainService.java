package com.loopers.domain.product;

import com.loopers.domain.brand.Brand;

public class ProductBrandDomainService {

    public ProductInfo findProductWithBrand(Product product, Brand brand, Long likeCount) {
        return ProductInfo.of(product, brand, likeCount);
    }
}
