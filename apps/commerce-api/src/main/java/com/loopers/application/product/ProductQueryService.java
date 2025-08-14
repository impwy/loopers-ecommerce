package com.loopers.application.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.loopers.application.provided.ProductFinder;
import com.loopers.application.required.ProductRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.product.Product;
import com.loopers.infrastructure.product.ProductWithLikeCount;
import com.loopers.interfaces.api.order.dto.OrderV1Dto.Request.CreateOrderRequest;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductQueryService implements ProductFinder {
    private final ProductRepository productRepository;

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
    public Page<ProductWithLikeCount> findWithLikeCount(String sortKey, Long brandId, Pageable pageable) {
        return productRepository.findWithLikeCount(sortKey, brandId, pageable);
    }

    @Override
    public Page<ProductWithLikeCount> findByBrandAndLikeCountDenormalization(String sortKey,
                                                                             List<Long> brandIds,
                                                                             Pageable pageable) {

        Page<ProductWithLikeCount> productWithBrands
                = productRepository.findByBrandDenormalization(sortKey, brandIds, pageable);

        return productWithBrands;
    }

    @Override
    public BigDecimal getTotalPrice(List<CreateOrderRequest> orderRequests) {
        List<Long> productIds = orderRequests.stream().map(CreateOrderRequest::productId).toList();
        List<Product> products = productRepository.findByIdIn(productIds);
        Map<Long, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));

        return orderRequests.stream()
                            .map(request -> productMap.get(request.productId())
                                                      .getTotalPrice(BigDecimal.valueOf(request.quantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<Long, Product> getProductMap(List<Long> productIds) {
        List<Product> products = productRepository.findByIdIn(productIds);
        return products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));
    }
}
