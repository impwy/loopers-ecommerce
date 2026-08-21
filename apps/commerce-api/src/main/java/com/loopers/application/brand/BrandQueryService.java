package com.loopers.application.brand;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.loopers.application.brand.provided.BrandFinder;
import com.loopers.application.brand.required.BrandRepository;
import com.loopers.application.product.provided.ProductFinder;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.ProductInfo;
import com.loopers.shared.error.CoreException;
import com.loopers.shared.error.ErrorType;
import com.loopers.shared.stereotype.ApplicationService;

import lombok.RequiredArgsConstructor;

@ApplicationService
@RequiredArgsConstructor
public class BrandQueryService implements BrandFinder {
    private final BrandRepository brandRepository;
    private final ProductFinder productFinder;

    @Override
    public Brand find(Long brandId) {
        return brandRepository.findById(brandId)
                              .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND,
                                                                   "브랜드를 찾을 수 없습니다. brandId:" + brandId));
    }

    @Override
    public BrandDetail findDetail(Long brandId, Pageable pageable) {
        Brand brand = find(brandId);
        Page<ProductInfo> products = productFinder.findWithLikeCount("latestAt", List.of(brandId), pageable);
        return BrandDetail.of(brand, products);
    }
}
