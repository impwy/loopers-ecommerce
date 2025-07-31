package com.loopers.application.order;

import java.util.List;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.OrderFinder;
import com.loopers.application.required.OrderRepository;
import com.loopers.domain.order.Order;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderQueryService implements OrderFinder {
    private final OrderRepository orderRepository;

    @Override
    public Order find(Long orderId) {
        return orderRepository.find(orderId)
                              .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND,
                                                                   "주문을 찾을 수 없습니다. orderId:" + orderId));
    }

    @Override
    public Order findByMemberId(Long memberId) {
        return orderRepository.findByMemberId(memberId)
                              .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND,
                                                                   "주문을 찾을 수 없습니다. memberId:" + memberId));
    }

    @Override
    public List<Order> findWithOrderItem(Long memberId) {
        return orderRepository.findWithOrderItem(memberId);
    }
}
