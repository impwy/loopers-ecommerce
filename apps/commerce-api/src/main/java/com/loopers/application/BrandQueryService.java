package com.loopers.application;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.BrandFinder;
import com.loopers.application.required.BrandRepository;
import com.loopers.domain.brand.Brand;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandQueryService implements BrandFinder {
    private final BrandRepository brandRepository;

    @Override
    public Brand find(Long brandId) {
        return brandRepository.find(brandId);
    }
}
