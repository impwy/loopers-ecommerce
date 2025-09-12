package com.loopers.application.product;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.application.provided.ProductFinder;
import com.loopers.application.provided.ProductOutboxRegister;
import com.loopers.application.provided.ProductRegister;
import com.loopers.domain.product.LikeDecrease;
import com.loopers.domain.product.LikeIncrease;
import com.loopers.domain.product.ProductBrandDomainService;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.outbox.CreateProductOutbox;
import com.loopers.domain.product.outbox.ProductEventOutbox;
import com.loopers.domain.product.ProductPayload.ProductEventType;
import com.loopers.infrastructure.product.ProductWithLikeCount;
import com.loopers.interfaces.api.product.dto.ProductV1Dto.Response.ProductInfoPageResponse;
import com.loopers.interfaces.api.product.dto.ProductV1Dto.Response.ProductsInfoResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductFinder productFinder;
    private final ProductBrandDomainService productBrandDomainService;
    private final ApplicationEventPublisher eventPublisher;
    private final ProductRegister productRegister;
    private final ProductOutboxRegister productOutboxRegister;

    @Transactional
    public ProductInfo findProductInfo(Long productId) {
        ProductInfo cachedProduct = productFinder.findCachedProduct(productId);
        return cachedProduct;
    }

    @Transactional
    public ProductInfoPageResponse findProductsInfo(String sort, List<Long> brandIds, Pageable pageable) {
        Page<ProductWithLikeCount> withLikeCount = productFinder.findWithLikeCount(sort, brandIds, pageable);

        List<ProductInfo> productInfos
                = withLikeCount.stream()
                               .map(p -> productBrandDomainService.findProductWithBrand(p.product(),
                                                                                        p.product().getBrand(),
                                                                                        p.likeCount()))
                               .toList();
        return ProductInfoPageResponse.from(new PageImpl<>(productInfos, pageable, withLikeCount.getTotalElements()));
    }

    @Transactional
    public ProductInfoPageResponse findProductsInfoDenormalization(String sort, List<Long> brandIds, Pageable pageable) {
        Page<ProductWithLikeCount> withLikeCount = productFinder.findByBrandAndLikeCountDenormalization(sort, brandIds, pageable);

        List<ProductInfo> productInfos
                = withLikeCount.stream()
                               .map(p -> productBrandDomainService.findProductWithBrand(p.product(),
                                                                                        p.product().getBrand(),
                                                                                        p.likeCount()))
                               .toList();
        return ProductInfoPageResponse.from(new PageImpl<>(productInfos, pageable, withLikeCount.getTotalElements()));
    }

    @Transactional
    public ProductInfoPageResponse findProductsInfoDenormalizationWithRedis(String sort, List<Long> brandIds, Pageable pageable) {
        Page<ProductWithLikeCount> withLikeCount = productFinder.findByBrandAndLikeCountDenormalizationWithRedis(sort,
                                                                                                                 brandIds,
                                                                                                                 pageable);

        List<ProductInfo> productInfos
                = withLikeCount.stream()
                               .map(p -> productBrandDomainService.findProductWithBrand(p.product(),
                                                                                        p.product().getBrand(),
                                                                                        p.likeCount()))
                               .toList();
        return ProductInfoPageResponse.from(new PageImpl<>(productInfos, pageable, withLikeCount.getTotalElements()));
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
                                                                          ProductEventType.PRODUCT_LIKE_INCREMENT,
                                                                          0L, ZonedDateTime.now());

        ProductEventOutbox productEventOutbox = productOutboxRegister.register(createProductOutbox);

        eventPublisher.publishEvent(new LikeDecrease(productEventOutbox.getId(), productId));
    }

    public ProductInfoPageResponse findProductRanking(String date, Pageable pageable) {
        return productFinder.findProductInfoWithRank(date, pageable);
    }
}
