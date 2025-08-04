package com.loopers.application.product;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.application.provided.ProductFinder;
import com.loopers.application.provided.ProductLikeFinder;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductBrandDomainService;
import com.loopers.domain.product.ProductInfo;
import com.loopers.interfaces.api.product.dto.ProductV1Dto.Response.ProductsInfoResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductFinder productFinder;
    private final ProductLikeFinder productLikeFinder;
    private final ProductBrandDomainService productBrandDomainService;

    @Transactional
    public ProductInfo findProductInfo(Long productId) {
        Product product = productFinder.find(productId);
        Brand brand = product.getBrand();
        Long likeCount = productLikeFinder.countByProductId(productId);
        return productBrandDomainService.findProductWithBrand(product, brand, likeCount);
    }

    /**
     * TODO: 정렬 조회는 손봐줘야 한다.
     */
    @Transactional
    public ProductsInfoResponse findProductsInfo(String sort) {
        List<Product> products = productFinder.findByConditions(Sort.by(Direction.DESC, sort));
        List<ProductInfo> productInfos = products.stream().map(product -> {
            Long likeCount = productLikeFinder.countByProductId(product.getId());
            ProductInfo productInfo = productBrandDomainService.findProductWithBrand(product, product.getBrand(), likeCount);
            return productInfo;
        }).toList();
        return ProductsInfoResponse.of(productInfos);
    }
}
