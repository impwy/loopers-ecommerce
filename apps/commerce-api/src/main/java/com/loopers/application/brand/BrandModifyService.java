package com.loopers.application.brand;

import com.loopers.application.brand.provided.BrandRegister;
import com.loopers.application.brand.required.BrandRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.shared.stereotype.ApplicationValidService;

import lombok.RequiredArgsConstructor;

@ApplicationValidService
@RequiredArgsConstructor
public class BrandModifyService implements BrandRegister {
    private final BrandRepository brandRepository;

    @Override
    public Brand create(Brand brand) {
        return brandRepository.save(brand);
    }
}
