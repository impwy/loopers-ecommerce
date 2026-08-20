package com.loopers.application.brand.required;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.loopers.adapter.webapi.brand.dto.BrandV1Dto.BrandCreateResponse;
import com.loopers.domain.brand.Brand;

public interface BrandRepository extends Repository<Brand, Long> {
    Brand save(Brand brand);

    Optional<Brand> findById(Long brandId);
}
