package com.loopers.application.brand.provided;

import com.loopers.application.brand.BrandCreateRequest;
import com.loopers.domain.brand.Brand;

public interface BrandRegister {
    Brand create(BrandCreateRequest createRequest);
}
