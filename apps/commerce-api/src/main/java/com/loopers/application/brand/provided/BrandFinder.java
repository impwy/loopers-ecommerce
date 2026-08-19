package com.loopers.application.brand.provided;

import com.loopers.domain.brand.Brand;

public interface BrandFinder {
    Brand find(Long brandId);
}
