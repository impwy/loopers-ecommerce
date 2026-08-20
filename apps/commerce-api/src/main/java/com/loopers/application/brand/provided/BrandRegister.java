package com.loopers.application.brand.provided;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandCreateRequest;

public interface BrandRegister {
    Brand create(BrandCreateRequest createRequest);
}
