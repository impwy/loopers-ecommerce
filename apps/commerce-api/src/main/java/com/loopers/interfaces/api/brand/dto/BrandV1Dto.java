package com.loopers.interfaces.api.brand.dto;

import java.util.List;

import com.loopers.domain.product.ProductInfo;

public class BrandV1Dto {
    public class Response {
        public record BrandInfoResponse(List<ProductInfo> productInfos) {
            public static BrandInfoResponse of(List<ProductInfo> productInfos) {
                return new BrandInfoResponse(productInfos);
            }
        }
    }
}
