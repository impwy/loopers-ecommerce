package com.loopers.infrastructure.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    List<Product> findByBrand(Brand brand);
}
