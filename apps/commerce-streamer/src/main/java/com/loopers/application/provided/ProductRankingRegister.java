package com.loopers.application.provided;

import java.util.List;

import com.loopers.interfaces.consumer.dto.ProductPayload;

public interface ProductRankingRegister {
    void rank(List<ProductPayload> productPayloads);
}
