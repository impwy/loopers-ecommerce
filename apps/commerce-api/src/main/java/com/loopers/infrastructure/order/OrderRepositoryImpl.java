package com.loopers.infrastructure.order;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.loopers.application.required.OrderRepository;
import com.loopers.domain.order.Order;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Optional<Order> find(Long orderId) {
        return orderJpaRepository.findById(orderId);
    }

    @Override
    public Optional<Order> findByMemberId(Long memberId) {
        return orderJpaRepository.findByMemberId(memberId);
    }
}
