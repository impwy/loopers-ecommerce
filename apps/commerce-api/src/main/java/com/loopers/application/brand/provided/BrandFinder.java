package com.loopers.application.brand.provided;

import org.springframework.data.domain.Pageable;

import com.loopers.adapter.webapi.brand.dto.BrandV1Dto.BrandDetailResponse;
import com.loopers.domain.brand.Brand;

public interface BrandFinder {
    Brand find(Long brandId);

    BrandDetailResponse findDetail(Long brandId, Pageable pageable);
}
