package com.loopers.infrastructure.orderitem;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.orderitem.OrderItem;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {
}
