package com.loopers.application.ranking;

import java.util.List;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.ProductRankingRegister;
import com.loopers.interfaces.consumer.dto.ProductPayload;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductRankingFacade {
    private final ProductRankingRegister productRankingRegister;

    public void rank(List<ProductPayload> messages) {
        productRankingRegister.rank(messages);
    }
}
