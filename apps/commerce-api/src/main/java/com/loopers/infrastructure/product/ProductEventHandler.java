package com.loopers.infrastructure.product;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.loopers.application.provided.ProductRegister;
import com.loopers.domain.product.LikeDecrease;
import com.loopers.domain.product.LikeIncrease;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductEventHandler {
    private final ProductRegister productRegister;

    @Async
    @EventListener
    public void handler(LikeIncrease event) {
        productRegister.increaseLike(event.productId());
    }

    @Async
    @EventListener
    public void handler(LikeDecrease event) {
        productRegister.decreaseLike(event.productId());
    }
}
