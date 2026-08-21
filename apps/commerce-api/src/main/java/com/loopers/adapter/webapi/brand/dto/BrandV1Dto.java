package com.loopers.adapter.webapi.brand.dto;

import java.time.LocalDate;

import org.springframework.data.domain.Page;

import com.loopers.application.brand.BrandDetail;
import com.loopers.application.brand.provided.ProductPage;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandProfile;
import com.loopers.domain.product.ProductInfo;

public class BrandV1Dto {
    /**
     * 브랜드 프로필과 해당 브랜드 상품의 페이지 정보를 함께 반환하는 응답입니다.
     */
    public record BrandDetailResponse(String name, String description, LocalDate since, ProductPage productPage) {
        public static BrandDetailResponse of(Brand brand, Page<ProductInfo> productPage) {
            return from(BrandDetail.of(brand, productPage));
        }

        public static BrandDetailResponse from(BrandDetail brandDetail) {
            return new BrandDetailResponse(brandDetail.name(), brandDetail.description(),
                                           brandDetail.since(), brandDetail.productPage());
        }
    }

    public record BrandCreateResponse(Long brandId, BrandProfile brandProfile) {
        public static BrandCreateResponse of(Brand brand) {
            return new BrandCreateResponse(brand.getId(), brand.getBrandProfile());
        }
    }
}
