package com.loopers.application;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.BrandRegister;
import com.loopers.application.required.BrandRepository;
import com.loopers.domain.brand.Brand;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandModifyService implements BrandRegister {
    private final BrandRepository brandRepository;

    @Override
    public Brand create(Brand brand) {
        return brandRepository.create(brand);
    }
}
