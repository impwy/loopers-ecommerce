package com.loopers.adapter.integration.jpa.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.loopers.application.product.required.ProductRepository;
import com.loopers.domain.product.Product;

import jakarta.persistence.LockModeType;

public interface ProductJpaRepository extends JpaRepository<Product, Long>, ProductRepository, ProductCustomRepository {
    @Override
    @Query("select p from Product p join fetch p.brand where p.id = :productId")
    Optional<Product> findByIdWithBrand(@Param("productId") Long productId);

    @Override
    @Query(value = "select p from Product p join fetch p.brand where p.id in :productIds",
           countQuery = "select count(p) from Product p where p.id in :productIds")
    Page<Product> findAllByIdInWithBrand(@Param("productIds") List<Long> productIds, Pageable pageable);

    @Override
    @Query(value = "select p from Product p join fetch p.brand order by p.likeCount desc",
           countQuery = "select count(p) from Product p")
    Page<Product> findAllByOrderByLikeCountDescWithBrand(Pageable pageable);

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id= :productId")
    Optional<Product> findByIdPessimisticLock(@Param("productId") Long productId);
}
