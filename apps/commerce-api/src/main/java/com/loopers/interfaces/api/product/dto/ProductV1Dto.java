package com.loopers.interfaces.api.product.dto;

import java.util.List;

import com.loopers.domain.product.ProductInfo;

public class ProductV1Dto {
    public class Response {
        public record ProductsInfoResponse(List<ProductInfo> productInfos) {
            public static ProductsInfoResponse of(List<ProductInfo> productInfos) {
                return new ProductsInfoResponse(productInfos);
            }
        }
    }
}
