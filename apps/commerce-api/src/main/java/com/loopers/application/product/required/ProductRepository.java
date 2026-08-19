package com.loopers.application.product.required;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;

import jakarta.persistence.LockModeType;

@NoRepositoryBean
public interface ProductRepository extends Repository<Product, Long>, ProductQueryRepository {
    Product save(Product product);

    Optional<Product> findById(Long productId);

    List<Product> findAll(Sort sort);

    List<Product> findByBrand(Brand brand);

    List<Product> findByIdIn(List<Long> productIds);

    Page<Product> findAllByIdIn(List<Long> productIds, Pageable pageable);

    Page<Product> findAllByOrderByLikeCountDesc(Pageable pageable);

    Optional<Product> findByIdPessimisticLock(Long productId);
}
