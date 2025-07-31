package com.loopers.infrastructure.order;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.order.Order;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByMemberId(Long memberId);
}
