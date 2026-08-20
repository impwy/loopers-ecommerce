package com.loopers.application.brand;

import com.loopers.application.brand.provided.BrandRegister;
import com.loopers.application.brand.required.BrandRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandCreateRequest;
import com.loopers.shared.stereotype.ApplicationValidService;

import lombok.RequiredArgsConstructor;

@ApplicationValidService
@RequiredArgsConstructor
public class BrandModifyService implements BrandRegister {
    private final BrandRepository brandRepository;

    @Override
    public Brand create(BrandCreateRequest createRequest) {
        Brand brand = Brand.create(createRequest.name(),
                                   createRequest.description(),
                                   createRequest.since());
        brand = brandRepository.save(brand);

        return brand;
    }
}
