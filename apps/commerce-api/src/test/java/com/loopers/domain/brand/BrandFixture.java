package com.loopers.domain.brand;

import java.time.LocalDate;

import com.loopers.application.brand.BrandCreateRequest;

public class BrandFixture {
    public static Brand createBrand() {
        return createBrand(LocalDate.of(2000, 1, 1));
    }

    public static Brand createBrand(LocalDate since) {
        return Brand.create("브랜드", "브랜드입니다.", since);
    }

    public static BrandCreateRequest createBrandCreateRequest() {
        return new BrandCreateRequest("브랜드", "브랜드입니다.", LocalDate.now());
    }
}
