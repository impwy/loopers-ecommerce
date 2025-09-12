package com.loopers.infrastructure.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Page<ProductWithLikeCount> findWithLikeCount(String sortKey, List<Long> brandIds, Pageable pageable) {
        return productJpaRepository.findByBrandAndLikeCount(sortKey, brandIds, pageable);
    }

    @Override
    public Page<ProductWithLikeCount> findByBrandDenormalizationWithLike(String sortKey, List<Long> brandIds, Pageable pageable) {
        return productJpaRepository.findByBrandDenormalizationWithLike(sortKey, brandIds, pageable);
    }

    @Override
    public Page<ProductWithBrand> findByBrandDenormalization(String sortKey,
                                                             List<Long> brandIds,
                                                             Pageable pageable) {
        return productJpaRepository.findByBrandDenormalization(sortKey, brandIds, pageable);
    }

    @Override
    public List<Product> findByIdIn(List<Long> productIds) {
        return productJpaRepository.findAllById(productIds);
    }

    @Override
    public Optional<Product> findByIdPessimisticLock(Long productId) {
        return productJpaRepository.findByIdPessimisticLock(productId);
    }

    @Override
    public Page<Product> findAllByIdIn(List<Long> productIds, Pageable pageable) {
        return productJpaRepository.findAllByIdIn(productIds, pageable);
    }
}
