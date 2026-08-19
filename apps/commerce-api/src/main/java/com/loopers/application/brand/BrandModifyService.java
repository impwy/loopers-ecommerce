package com.loopers.application.brand;

import org.springframework.stereotype.Service;

import com.loopers.application.brand.provided.BrandRegister;
import com.loopers.application.brand.required.BrandRepository;
import com.loopers.domain.brand.Brand;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BrandModifyService implements BrandRegister {
    private final BrandRepository brandRepository;

    @Override
    public Brand create(Brand brand) {
        return brandRepository.save(brand);
    }
}
