package com.loopers.application.provided;

import java.util.List;

import com.loopers.interfaces.consumer.dto.ProductPayload;

public interface ProductMetricsRegister {
    void handleProductEvent(List<ProductPayload> productPayloads);
}
