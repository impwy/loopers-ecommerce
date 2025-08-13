package com.loopers.interfaces.api.product;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.loopers.application.product.ProductFacade;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.product.dto.ProductV1Dto.Response.ProductInfoPageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductV1ApiController {
    private final ProductFacade productFacade;

    @GetMapping
    public ApiResponse<ProductInfoPageResponse> getProductsInfo(@RequestParam String sort, Pageable pageable) {
        ProductInfoPageResponse productsInfoResponse = productFacade.findProductsInfo(sort, pageable);
        return ApiResponse.success(productsInfoResponse);
    }
}
