package com.loopers.application.brand.provided;

import org.springframework.data.domain.Pageable;

import com.loopers.application.brand.BrandDetail;
import com.loopers.domain.brand.Brand;

public interface BrandFinder {
    Brand find(Long brandId);

    BrandDetail findDetail(Long brandId, Pageable pageable);
}
