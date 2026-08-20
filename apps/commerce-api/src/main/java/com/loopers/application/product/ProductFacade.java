package com.loopers.application.product;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.adapter.webapi.product.dto.ProductV1Dto.Response.ProductInfoPageResponse;
import com.loopers.application.product.provided.ProductFinder;
import com.loopers.application.product.provided.ProductOutboxRegister;
import com.loopers.application.product.provided.ProductRegister;
import com.loopers.domain.product.LikeDecrease;
import com.loopers.domain.product.LikeIncrease;
import com.loopers.domain.product.ProductInfoWithRank;
import com.loopers.domain.product.ProductPayload.ProductEventType;
import com.loopers.domain.product.outbox.CreateProductOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox;
import com.loopers.shared.stereotype.ApplicationValidService;

import lombok.RequiredArgsConstructor;

@ApplicationValidService
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductFinder productFinder;
    private final ApplicationEventPublisher eventPublisher;
    private final ProductRegister productRegister;
    private final ProductOutboxRegister productOutboxRegister;

    @Transactional
    public ProductInfoWithRank findProductInfo(Long productId) {
        ProductInfoWithRank cachedProduct = productFinder.findCachedProduct(productId);
        return cachedProduct;
    }

    @Transactional
    public ProductInfoPageResponse findProductsInfo(String sort, List<Long> brandIds, Pageable pageable) {
        return ProductInfoPageResponse.from(productFinder.findWithLikeCount(sort, brandIds, pageable));
    }

    @Transactional
    public ProductInfoPageResponse findProductsInfoDenormalization(String sort, List<Long> brandIds, Pageable pageable) {
        return ProductInfoPageResponse.from(
                productFinder.findByBrandAndLikeCountDenormalization(sort, brandIds, pageable));
    }

    @Transactional
    public ProductInfoPageResponse findProductsInfoDenormalizationWithRedis(String sort, List<Long> brandIds, Pageable pageable) {
        return ProductInfoPageResponse.from(
                productFinder.findByBrandAndLikeCountDenormalizationWithRedis(sort, brandIds, pageable));
    }

    @Transactional
    public void increaseLikeCount(Long productId) {
        productRegister.increaseLike(productId);

        String uuid = UUID.randomUUID().toString();
        CreateProductOutbox createProductOutbox = new CreateProductOutbox(productId, uuid,
                                                                          ProductEventType.PRODUCT_LIKE_INCREMENT,
                                                                          0L, ZonedDateTime.now());

        ProductEventOutbox productEventOutbox = productOutboxRegister.register(createProductOutbox);

        eventPublisher.publishEvent(new LikeIncrease(productEventOutbox.getId(), productId));
    }

    @Transactional
    public void decreaseLikeCount(Long productId) {
        productRegister.decreaseLike(productId);

        String uuid = UUID.randomUUID().toString();
        CreateProductOutbox createProductOutbox = new CreateProductOutbox(productId, uuid,
                                                                          ProductEventType.PRODUCT_LIKE_DECREMENT,
                                                                          0L, ZonedDateTime.now());

        ProductEventOutbox productEventOutbox = productOutboxRegister.register(createProductOutbox);

        eventPublisher.publishEvent(new LikeDecrease(productEventOutbox.getId(), productId));
    }
}
