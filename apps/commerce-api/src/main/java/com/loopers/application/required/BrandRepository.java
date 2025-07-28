package com.loopers.application.required;

import com.loopers.domain.brand.Brand;

public interface BrandRepository {
    Brand create(Brand brand);

    Brand find(Long brandId);
}
