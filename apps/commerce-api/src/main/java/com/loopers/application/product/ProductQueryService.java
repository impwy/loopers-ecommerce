package com.loopers.application.product;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.loopers.application.provided.ProductFinder;
import com.loopers.application.required.CachedPage;
import com.loopers.application.required.ProductRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.infrastructure.product.ProductWithLikeCount;
import com.loopers.infrastructure.redis.RedisRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductQueryService implements ProductFinder {
    private final ProductRepository productRepository;
    private final RedisRepository redisRepository;

    @Override
    public Product find(Long productId) {
        Product product = productRepository.find(productId)
                                           .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));
        return product;
    }

    @Override
    public List<Product> findByConditions(Sort sort) {
        return productRepository.findByConditions(sort);
    }

    @Override
    public List<Product> findByBrand(Brand brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public Page<ProductWithLikeCount> findWithLikeCount(String sortKey, List<Long> brandIds, Pageable pageable) {
        return productRepository.findWithLikeCount(sortKey, brandIds, pageable);
    }

    @Override
    public Page<ProductWithLikeCount> findByBrandAndLikeCountDenormalization(String sortKey, List<Long> brandIds,
                                                                             Pageable pageable) {

        Page<ProductWithLikeCount> productWithLikeCounts
                = productRepository.findByBrandDenormalizationWithLike(sortKey, brandIds, pageable);
        return productWithLikeCounts;
    }

    @Override
    public Page<ProductWithLikeCount> findByBrandAndLikeCountDenormalizationWithRedis(String sortKey,
                                                                                      List<Long> brandIds,
                                                                                      Pageable pageable) {
        String redisKey = String.format("product:brands:%s:sort:%s:page:%d",
                                        String.join("-", brandIds.stream().map(String::valueOf).toList()),
                                        sortKey, pageable.getPageNumber());

        Optional<CachedPage<ProductWithLikeCount>> cachedPageOpt =
                redisRepository.get(redisKey, new TypeReference<>() {});

        if (cachedPageOpt.isPresent()) {
            return cachedPageOpt.get().toPage(pageable);
        }

        Page<ProductWithLikeCount> productWithLikeCounts
                = productRepository.findByBrandDenormalizationWithLike(sortKey, brandIds, pageable);

        List<ProductWithLikeCount> content = productWithLikeCounts.toList();

        PageImpl<ProductWithLikeCount> pageResult = new PageImpl<>(content, pageable,
                                                                   productWithLikeCounts.getTotalElements());

        if (pageable.getPageNumber() <= 1) {
            CachedPage<ProductWithLikeCount> cached = CachedPage.of(pageResult);
            redisRepository.save(redisKey, cached, Duration.ofMinutes(5));
        }

        return pageResult;
    }

    @Override
    public BigDecimal getTotalPrice(List<ProductTotalAmountRequest> productTotalAmountRequests) {
        List<Long> productIds = productTotalAmountRequests.stream().map(ProductTotalAmountRequest::productId).toList();
        List<Product> products = productRepository.findByIdIn(productIds);
        Map<Long, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));

        return productTotalAmountRequests.stream()
                            .map(request -> productMap.get(request.productId())
                                                      .getTotalPrice(BigDecimal.valueOf(request.quantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<Long, Product> getProductMap(List<Long> productIds) {
        List<Product> products = productRepository.findByIdIn(productIds);
        return products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));
    }

    @Override
    public Product findProductPessimisticLock(Long productId) {
        return productRepository.findByIdPessimisticLock(productId)
                                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND,
                                                                     "상품을 찾을 수 없습니다 " + productId));
    }
}
