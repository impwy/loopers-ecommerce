package com.loopers.domain.brand;

import java.time.LocalDate;

import com.loopers.application.brand.BrandCreateRequest;

import org.instancio.Instancio;

import static org.instancio.Select.field;

public class BrandFixture {
    private static final String DEFAULT_NAME = "브랜드";
    private static final String DEFAULT_DESCRIPTION = "브랜드입니다.";
    private static final LocalDate DEFAULT_SINCE = LocalDate.now();

    public static Brand createBrand() {
        return Instancio.of(Brand.class)
                        .ignore(field(Brand::getId))
                        .generate(field(Brand::getName), gen -> gen.string().minLength(1).maxLength(20))
                        .generate(field(Brand::getDescription), gen -> gen.string().minLength(1).maxLength(50))
                        .set(field(Brand::getBrandProfile), BrandProfile.create(DEFAULT_SINCE))
                        .create();
    }

    public static Brand createBrand(LocalDate since) {
        return createBrand(DEFAULT_NAME, DEFAULT_DESCRIPTION, since);
    }

    public static Brand createBrand(String name) {
        return createBrand(name, DEFAULT_DESCRIPTION, DEFAULT_SINCE);
    }

    public static Brand createBrand(String name, String description, LocalDate since) {
        return Instancio.of(Brand.class)
                        .ignore(field(Brand::getId))
                        .set(field(Brand::getName), name)
                        .set(field(Brand::getDescription), description)
                        .set(field(Brand::getBrandProfile), BrandProfile.create(since))
                        .create();
    }

    public static BrandCreateRequest createBrandCreateRequest() {
        return Instancio.of(BrandCreateRequest.class)
                        .generate(field(BrandCreateRequest::name), gen -> gen.string().minLength(1).maxLength(20))
                        .generate(field(BrandCreateRequest::description), gen -> gen.string().minLength(1).maxLength(50))
                        .set(field(BrandCreateRequest::since), DEFAULT_SINCE)
                        .create();
    }

    public static BrandCreateRequest createBrandCreateRequest(String name, String description, LocalDate since) {
        return Instancio.of(BrandCreateRequest.class)
                        .set(field(BrandCreateRequest::name), name)
                        .set(field(BrandCreateRequest::description), description)
                        .set(field(BrandCreateRequest::since), since)
                        .create();
    }
}
