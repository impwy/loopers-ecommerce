package com.loopers.application.brand.required;

import org.springframework.data.repository.Repository;

import com.loopers.domain.brand.Brand;

public interface BrandRepository extends Repository<Brand, Long> {
    Brand save(Brand brand);

    Brand find(Long brandId);
}
