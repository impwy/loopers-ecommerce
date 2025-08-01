package com.loopers.infrastructure.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.loopers.application.required.ProductRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Optional<Product> find(Long productId) {
        return productJpaRepository.findById(productId);
    }

    @Override
    public List<Product> findByConditions(Sort sort) {
        return productJpaRepository.findAll(sort);
    }

    @Override
    public List<Product> findByBrand(Brand brand) {
        return productJpaRepository.findByBrand(brand);
    }
}
