package com.loopers.application.brand;

import java.util.List;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.BrandFinder;
import com.loopers.application.provided.ProductFinder;
import com.loopers.application.provided.ProductLikeFinder;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductBrandDomainService;
import com.loopers.domain.product.ProductInfo;
import com.loopers.interfaces.api.brand.dto.BrandV1Dto.Response.BrandInfoResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BrandFacade {
    private final BrandFinder brandFinder;
    private final ProductBrandDomainService productBrandDomainService;
    private final ProductLikeFinder productLikeFinder;
    private final ProductFinder productFinder;

    public BrandInfoResponse findBrandProductInfo(Long brandId) {
        Brand brand = brandFinder.find(brandId);
        List<Product> products = productFinder.findByBrand(brand);
        List<ProductInfo> productInfos
                = products.stream()
                          .map(product -> {
                              Long likeCount = productLikeFinder.countByProductId(product.getId());
                              return productBrandDomainService.findProductWithBrand(product, brand, likeCount);
                          })
                          .toList();
        return BrandInfoResponse.of(productInfos);
    }
}
