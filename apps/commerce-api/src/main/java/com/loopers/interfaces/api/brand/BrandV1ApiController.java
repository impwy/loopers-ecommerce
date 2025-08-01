package com.loopers.interfaces.api.brand;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loopers.application.brand.BrandFacade;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.brand.dto.BrandV1Dto.Response.BrandInfoResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/brands")
public class BrandV1ApiController implements BrandV1ApiSpec {
    private final BrandFacade brandFacade;

    @Override
    @GetMapping("/{brandId}")
    public ApiResponse<BrandInfoResponse> find(@PathVariable Long brandId) {
        BrandInfoResponse brandInfoResponse = brandFacade.findBrandProductInfo(brandId);
        return ApiResponse.success(brandInfoResponse);
    }
}
