package com.loopers.infrastructure.orderitem;

import org.springframework.stereotype.Repository;

import com.loopers.application.required.OrderItemRepository;
import com.loopers.domain.orderitem.OrderItem;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {
    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public OrderItem save(OrderItem orderItem) {
        return orderItemJpaRepository.save(orderItem);
    }
}
