package com.loopers.application.brand.required;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandFixture;
import com.loopers.support.stereotype.ApplicationJpaServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationJpaServiceTest
@RequiredArgsConstructor
class BrandRepositoryTest {
    final BrandRepository brandRepository;
    final TestEntityManager entityManager;

    @Test
    void save() {
        Brand brand = BrandFixture.createBrand();

        Brand save = brandRepository.save(brand);

        assertThat(save.getId()).isNotNull();

        assertThat(save.getName()).isEqualTo(brand.getName());

        assertThat(save.getCreatedAt()).isNotNull();
    }

    @Test
    void findById() {
        Brand brand = brandRepository.save(BrandFixture.createBrand());
        entityManager.flush();
        entityManager.clear();

        Brand found = brandRepository.findById(brand.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(brand.getId());
        assertThat(found.getName()).isEqualTo(brand.getName());
    }
}
