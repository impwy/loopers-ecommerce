package com.loopers.infrastructure.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;

import jakarta.persistence.LockModeType;

public interface ProductJpaRepository extends JpaRepository<Product, Long>, ProductQueryDslRepository {
    List<Product> findByBrand(Brand brand);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id= :productId")
    Optional<Product> findByIdPessimisticLock(@Param("productId") Long productId);

    Page<Product> findAllByOrderByLikeCountDesc(Pageable pageable);
}
