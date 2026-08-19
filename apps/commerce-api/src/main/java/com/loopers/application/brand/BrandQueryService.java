package com.loopers.application.brand;

import org.springframework.stereotype.Service;

import com.loopers.application.brand.provided.BrandFinder;
import com.loopers.application.brand.required.BrandRepository;
import com.loopers.domain.brand.Brand;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BrandQueryService implements BrandFinder {
    private final BrandRepository brandRepository;

    @Override
    public Brand find(Long brandId) {
        return brandRepository.find(brandId);
    }
}
