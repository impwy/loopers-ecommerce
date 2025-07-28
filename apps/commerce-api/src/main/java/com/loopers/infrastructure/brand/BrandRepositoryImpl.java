package com.loopers.infrastructure.brand;

import org.springframework.stereotype.Repository;

import com.loopers.application.required.BrandRepository;
import com.loopers.domain.brand.Brand;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BrandRepositoryImpl implements BrandRepository {
    private final BrandJpaRepository brandJpaRepository;

    public Brand create(Brand brand) {
        return brandJpaRepository.save(brand);
    }
}
