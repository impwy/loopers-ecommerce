package com.loopers.application.brand;

import java.time.LocalDate;

import org.springframework.data.domain.Page;

import com.loopers.application.brand.provided.ProductPage;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.ProductInfo;

public record BrandDetail(String name, String description, LocalDate since, ProductPage productPage) {
    public static BrandDetail of(Brand brand, Page<ProductInfo> productPage) {
        return new BrandDetail(brand.getName(), brand.getDescription(),
                               brand.getBrandProfile().getSince(), ProductPage.from(productPage));
    }
}
