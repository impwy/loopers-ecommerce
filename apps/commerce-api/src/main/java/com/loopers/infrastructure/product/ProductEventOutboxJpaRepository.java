package com.loopers.infrastructure.product;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.product.outbox.ProductEventOutbox;

public interface ProductEventOutboxJpaRepository extends JpaRepository<ProductEventOutbox, Long> {
}
