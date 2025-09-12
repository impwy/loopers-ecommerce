package com.loopers.interfaces.api.product;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.loopers.application.product.ProductFacade;
import com.loopers.domain.product.ProductInfo;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.product.dto.ProductV1Dto.Response.ProductInfoPageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductV1ApiController implements ProductV1ApiSpec {
    private final ProductFacade productFacade;

    @Override
    @GetMapping("/{productId}")
    public ApiResponse<ProductInfo> getProductInfo(@PathVariable Long productId) {
        ProductInfo productInfo = productFacade.findProductInfo(productId);
        return ApiResponse.success(productInfo);
    }

    @GetMapping
    @Override
    public ApiResponse<ProductInfoPageResponse> getProductsInfo(@RequestParam String sort,
                                                                @RequestParam List<Long> brandIds,
                                                                Pageable pageable) {
        ProductInfoPageResponse productsInfoResponse = productFacade.findProductsInfo(sort, brandIds, pageable);
        return ApiResponse.success(productsInfoResponse);
    }

    @GetMapping("/denormalization")
    @Override
    public ApiResponse<ProductInfoPageResponse> getProductsInfoDenormalization(@RequestParam String sort,
                                                                               @RequestParam List<Long> brandIds,
                                                                               Pageable pageable) {
        ProductInfoPageResponse productsInfoResponse = productFacade.findProductsInfoDenormalization(sort, brandIds, pageable);
        return ApiResponse.success(productsInfoResponse);
    }

    @GetMapping("/redis")
    @Override
    public ApiResponse<ProductInfoPageResponse> getProductsInfoDenormalizationWithRedis(@RequestParam String sort,
                                                                                        @RequestParam List<Long> brandIds,
                                                                                        Pageable pageable) {
        ProductInfoPageResponse productsInfoResponse =
                productFacade.findProductsInfoDenormalizationWithRedis(sort, brandIds, pageable);
        return ApiResponse.success(productsInfoResponse);
    }

    @GetMapping("/rankings")
    @Override
    public ApiResponse<ProductInfoPageResponse> getProductRanking(@RequestParam String date,
                                                               Pageable pageable) {
        ProductInfoPageResponse productInfoWithRank = productFacade.findProductRanking(date, pageable);
        return ApiResponse.success(productInfoWithRank);
    }
}
