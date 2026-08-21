package com.loopers.adapter.webapi.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.loopers.adapter.webapi.ApiResponse;
import com.loopers.adapter.webapi.product.dto.ProductV1Dto.ProductInfoPageResponse;
import com.loopers.application.product.ProductFacade;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductInfoWithRank;
import com.loopers.shared.stereotype.WebApiAdapter;

import lombok.RequiredArgsConstructor;

@WebApiAdapter
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductV1ApiController implements ProductV1ApiSpec {
    private final ProductFacade productFacade;

    @Override
    @GetMapping("/{productId}")
    public ApiResponse<ProductInfoWithRank> getProductInfo(@PathVariable Long productId) {
        ProductInfoWithRank productInfo = productFacade.findProductInfo(productId);
        return ApiResponse.success(productInfo);
    }

    @GetMapping
    @Override
    public ApiResponse<ProductInfoPageResponse> getProductsInfo(@RequestParam String sort,
                                                                @RequestParam List<Long> brandIds,
                                                                Pageable pageable) {
        Page<ProductInfo> productsInfo = productFacade.findProductsInfo(sort, brandIds, pageable);
        return ApiResponse.success(ProductInfoPageResponse.from(productsInfo));
    }

    @GetMapping("/denormalization")
    @Override
    public ApiResponse<ProductInfoPageResponse> getProductsInfoDenormalization(@RequestParam String sort,
                                                                               @RequestParam List<Long> brandIds,
                                                                               Pageable pageable) {
        Page<ProductInfo> productsInfo = productFacade.findProductsInfoDenormalization(sort, brandIds, pageable);
        return ApiResponse.success(ProductInfoPageResponse.from(productsInfo));
    }

    @GetMapping("/redis")
    @Override
    public ApiResponse<ProductInfoPageResponse> getProductsInfoDenormalizationWithRedis(@RequestParam String sort,
                                                                                        @RequestParam List<Long> brandIds,
                                                                                        Pageable pageable) {
        Page<ProductInfo> productsInfo =
                productFacade.findProductsInfoDenormalizationWithRedis(sort, brandIds, pageable);
        return ApiResponse.success(ProductInfoPageResponse.from(productsInfo));
    }
}
