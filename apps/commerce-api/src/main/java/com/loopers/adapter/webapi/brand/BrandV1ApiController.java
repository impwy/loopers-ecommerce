package com.loopers.adapter.webapi.brand;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.loopers.adapter.webapi.ApiResponse;
import com.loopers.adapter.webapi.brand.dto.BrandV1Dto.BrandCreateResponse;
import com.loopers.adapter.webapi.brand.dto.BrandV1Dto.BrandDetailResponse;
import com.loopers.application.brand.BrandCreateRequest;
import com.loopers.application.brand.BrandDetail;
import com.loopers.application.brand.provided.BrandFinder;
import com.loopers.application.brand.provided.BrandRegister;
import com.loopers.domain.brand.Brand;
import com.loopers.shared.stereotype.WebApiAdapter;

import lombok.RequiredArgsConstructor;

@WebApiAdapter
@RequiredArgsConstructor
@RequestMapping("/api/v1/brands")
public class BrandV1ApiController implements BrandV1ApiSpec {
    private final BrandFinder brandFinder;
    private final BrandRegister brandRegister;

    @Override
    @GetMapping("/{brandId}")
    public ApiResponse<BrandDetailResponse> find(@PathVariable Long brandId, Pageable pageable) {
        BrandDetail brandDetail = brandFinder.findDetail(brandId, pageable);
        return ApiResponse.success(BrandDetailResponse.from(brandDetail));
    }

    @Override
    @PostMapping
    public ApiResponse<BrandCreateResponse> create(@RequestBody BrandCreateRequest createRequest) {
        Brand brand = brandRegister.create(createRequest);
        return ApiResponse.success(BrandCreateResponse.of(brand));
    }
}
