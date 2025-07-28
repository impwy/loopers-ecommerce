package com.loopers.infrastructure.product;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.product.Product;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
}
