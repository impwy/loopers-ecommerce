package com.loopers.application.product;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.fasterxml.jackson.core.type.TypeReference;
import com.loopers.application.inmemory.required.InMemoryRepository;
import com.loopers.application.product.provided.ProductFinder;
import com.loopers.application.product.required.ProductRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductInfoWithRank;
import com.loopers.shared.CachedPage;
import com.loopers.shared.error.CoreException;
import com.loopers.shared.error.ErrorType;
import com.loopers.shared.stereotype.ApplicationService;

import lombok.RequiredArgsConstructor;

@ApplicationService
@RequiredArgsConstructor
public class ProductQueryService implements ProductFinder {
    private final ProductRepository productRepository;
    private final InMemoryRepository inMemoryRepository;

    private static final Function<String, String> PRODUCT_RANKING_KEY = key -> "ranking:all:" + key;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public Product find(Long productId) {
        Product product = productRepository.findById(productId)
                                           .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));
        return product;
    }

    @Override
    public ProductInfoWithRank findCachedProduct(Long productId) {
        String redisKey = String.format("product:%d", productId);
        Optional<ProductInfo> productInfoOpt = inMemoryRepository.get(redisKey, new TypeReference<>() {});
        Long rank = inMemoryRepository.getRank(PRODUCT_RANKING_KEY.apply(LocalDate.now().format(formatter)), productId);

        if (productInfoOpt.isPresent()) {
            return ProductInfoWithRank.of(productInfoOpt.get(), rank);
        }

        Product product = productRepository.findByIdWithBrand(productId)
                                           .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));

        ProductInfo productInfo = ProductInfo.of(product, product.getBrand(), product.getLikeCount());
        inMemoryRepository.save(redisKey, productInfo, Duration.ofMinutes(5));

        return ProductInfoWithRank.of(productInfo, rank);
    }

    @Override
    public List<Product> findByConditions(Sort sort) {
        return productRepository.findAll(sort);
    }

    @Override
    public List<Product> findByBrand(Brand brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public Page<ProductInfo> findWithLikeCount(String sortKey, List<Long> brandIds, Pageable pageable) {
        return productRepository.findWithLikeCount(sortKey, brandIds, pageable);
    }

    @Override
    public Page<ProductInfo> findByBrandAndLikeCountDenormalization(String sortKey, List<Long> brandIds,
                                                                    Pageable pageable) {

        return productRepository.findByBrandDenormalizationWithLike(sortKey, brandIds, pageable);
    }

    @Override
    public Page<ProductInfo> findByBrandAndLikeCountDenormalizationWithRedis(String sortKey,
                                                                             List<Long> brandIds,
                                                                             Pageable pageable) {
        String redisKey = String.format("product:brands:%s:sort:%s:page:%d",
                                        String.join("-", brandIds.stream().map(String::valueOf).toList()),
                                        sortKey, pageable.getPageNumber());

        Optional<CachedPage<ProductInfo>> cachedPageOpt =
                inMemoryRepository.get(redisKey, new TypeReference<>() {});

        if (cachedPageOpt.isPresent()) {
            return cachedPageOpt.get().toPage(pageable);
        }

        Page<ProductInfo> productInfoPage =
                productRepository.findByBrandDenormalizationWithLike(sortKey, brandIds, pageable);

        if (pageable.getPageNumber() <= 1) {
            CachedPage<ProductInfo> cached = CachedPage.of(productInfoPage);
            inMemoryRepository.save(redisKey, cached, Duration.ofMinutes(5));
        }

        return productInfoPage;
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

    @Override
    public Page<ProductInfo> findProductInfoWithRank(String date, Pageable pageable) {
        String redisKey = PRODUCT_RANKING_KEY.apply(date);
        Set<TypedTuple<Object>> typedTuples = inMemoryRepository.zReverRange(redisKey, 0L, 100L);
        List<Long> productIds = typedTuples.stream().map(TypedTuple::getValue).map(Long.class::cast).toList();

        return findProductInfosByIds(productIds, pageable);
    }

    @Override
    public Page<ProductInfo> findProductInfosByIds(List<Long> productIds, Pageable pageable) {
        if (productIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0L);
        }

        Page<Product> products = productRepository.findAllByIdInWithBrand(productIds, pageable);
        List<ProductInfo> productInfos = products.stream()
                                                 .map(product -> ProductInfo.of(product, product.getBrand(),
                                                                                product.getLikeCount()))
                                                 .toList();
        return new PageImpl<>(productInfos, pageable, products.getTotalElements());
    }

    @Override
    public Page<ProductInfo> findProductInfosByLikeCountDesc(Pageable pageable) {
        Page<Product> products = productRepository.findAllByOrderByLikeCountDescWithBrand(pageable);
        List<ProductInfo> productInfos = products.stream()
                                                 .map(product -> ProductInfo.of(product, product.getBrand(),
                                                                                product.getLikeCount()))
                                                 .toList();
        return new PageImpl<>(productInfos, pageable, products.getTotalElements());
    }

}
