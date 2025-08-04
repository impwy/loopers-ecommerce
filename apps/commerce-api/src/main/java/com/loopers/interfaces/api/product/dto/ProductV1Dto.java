package com.loopers.interfaces.api.product.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.loopers.domain.product.ProductInfo;

public class ProductV1Dto {
    public class Response {
        public record ProductsInfoResponse(List<ProductInfo> productInfos) {
            public static ProductsInfoResponse of(List<ProductInfo> productInfos) {
                return new ProductsInfoResponse(productInfos);
            }
        }

        public record ProductInfoPageResponse(List<ProductInfo> content, int pageNumber, int pageSize, int totalPages,
                                              long totalElements) {
            public static ProductInfoPageResponse from(Page<ProductInfo> page) {
                return new ProductInfoPageResponse(
                        page.getContent(),
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalPages(),
                        page.getTotalElements()
                );
            }
        }
    }
}
