package com.loopers.infrastructure.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderNo;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByMemberId(Long memberId);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems where o.memberId =:memberId")
    List<Order> findWithOrderItem(@Param("memberId") Long memberId);

    Optional<Order> findByOrderNo(OrderNo orderNo);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.orderNo = :orderNo")
    Optional<Order> findByOrderNoWithItems(@Param("orderNo") OrderNo orderNo);
}
