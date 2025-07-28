package com.loopers.infrastructure.brand;

import org.springframework.stereotype.Repository;

import com.loopers.application.required.BrandRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BrandRepositoryImpl implements BrandRepository {
    private final BrandJpaRepository brandJpaRepository;

    @Override
    public Brand create(Brand brand) {
        return brandJpaRepository.save(brand);
    }

    @Override
    public Brand find(Long brandId) {
        return brandJpaRepository.findById(brandId).orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "없는 브랜드 입니다."));
    }
}
