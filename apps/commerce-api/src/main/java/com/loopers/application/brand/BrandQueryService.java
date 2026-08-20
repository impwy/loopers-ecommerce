package com.loopers.application.brand;

import com.loopers.application.brand.provided.BrandFinder;
import com.loopers.application.brand.required.BrandRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.shared.error.CoreException;
import com.loopers.shared.error.ErrorType;
import com.loopers.shared.stereotype.ApplicationService;

import lombok.RequiredArgsConstructor;

@ApplicationService
@RequiredArgsConstructor
public class BrandQueryService implements BrandFinder {
    private final BrandRepository brandRepository;

    @Override
    public Brand find(Long brandId) {
        return brandRepository.findById(brandId)
                              .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND,
                                                                   "브랜드를 찾을 수 없습니다. brandId:" + brandId));
    }
}
