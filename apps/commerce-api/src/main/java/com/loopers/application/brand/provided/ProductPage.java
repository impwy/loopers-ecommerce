package com.loopers.application.brand.provided;

import java.util.List;

import org.springframework.data.domain.Page;

import com.loopers.domain.product.ProductInfo;

/** API 응답 전용 상품 페이지입니다. */
public record ProductPage(List<ProductInfo> content, int pageNumber, int pageSize, int totalPages, long totalElements) {
    public static ProductPage from(Page<ProductInfo> page) {
        return new ProductPage(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
