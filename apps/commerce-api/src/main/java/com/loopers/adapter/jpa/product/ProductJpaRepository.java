package com.loopers.adapter.jpa.product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.loopers.application.product.required.ProductRepository;
import com.loopers.domain.product.Product;

import jakarta.persistence.LockModeType;

public interface ProductJpaRepository extends JpaRepository<Product, Long>, ProductRepository, ProductCustomRepository {
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id= :productId")
    Optional<Product> findByIdPessimisticLock(@Param("productId") Long productId);
}
