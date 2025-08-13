package com.loopers.application.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.application.provided.ProductFinder;
import com.loopers.application.provided.ProductLikeFinder;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductBrandDomainService;
import com.loopers.domain.product.ProductInfo;
import com.loopers.infrastructure.product.ProductQueryDslRepositoryImpl.ProductWithLikeCount;
import com.loopers.interfaces.api.product.dto.ProductV1Dto.Response.ProductInfoPageResponse;

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

    @Transactional
    public ProductInfoPageResponse findProductsInfo(String sort, Pageable pageable) {
        Page<ProductWithLikeCount> withLikeCount = productFinder.findWithLikeCount(sort, pageable);

        List<ProductInfo> productInfos
                = withLikeCount.stream()
                               .map(p -> productBrandDomainService.findProductWithBrand(p.product(),
                                                                                        p.product().getBrand(),
                                                                                        p.likeCount()))
                               .toList();
        return ProductInfoPageResponse.from(new PageImpl<>(productInfos, pageable, withLikeCount.getTotalElements()));
    }
}
