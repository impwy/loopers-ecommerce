package com.loopers.interfaces.api.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loopers.application.product.ProductFacade;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.product.dto.ProductV1Dto.Response.ProductsInfoResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductV1ApiController {
    private final ProductFacade productFacade;

    @GetMapping("/{sort}")
    public ApiResponse<ProductsInfoResponse> getProductsInfo(@PathVariable String sort) {
        ProductsInfoResponse productsInfoResponse = productFacade.findProductsInfo(sort);
        return ApiResponse.success(productsInfoResponse);
    }
}
