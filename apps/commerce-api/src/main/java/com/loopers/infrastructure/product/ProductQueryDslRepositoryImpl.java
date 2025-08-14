package com.loopers.infrastructure.product;

import static com.loopers.domain.brand.QBrand.brand;
import static com.loopers.domain.like.QProductLike.productLike;
import static com.loopers.domain.product.QProduct.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import com.loopers.domain.product.QProduct;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQueryFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductQueryDslRepositoryImpl implements ProductQueryDslRepository {
    private final JPQLQueryFactory queryFactory;

    @Override
    public Page<ProductWithLikeCount> findByBrandAndLikeCount(String sortKey, Long brandId, Pageable pageable) {
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortKey, product);

        List<ProductWithLikeCount> content = queryFactory
                .select(Projections.constructor(ProductWithLikeCount.class,
                                                product,
                                                brand,
                                                productLike.countDistinct()
                ))
                .from(product)
                .leftJoin(product.brand, brand).fetchJoin()
                .leftJoin(productLike).on(productLike.product.eq(product))
                .where(brand.id.eq(brandId))
                .groupBy(product.id)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                () -> Optional.ofNullable(queryFactory
                                                  .select(product.id.countDistinct())
                                                  .from(product)
                                                  .leftJoin(productLike).on(productLike.product.eq(product))
                                                  .fetchOne()
                ).orElse(0L)
        );
    }

    @Override
    public Page<ProductWithLikeCount> findByBrandDenormalization(String sortKey,
                                                                 List<Long> brandIds,
                                                                 Pageable pageable) {
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortKey, product);

        List<ProductWithLikeCount> content = queryFactory
                .select(Projections.constructor(ProductWithLikeCount.class,
                                                product,
                                                brand
                ))
                .from(product)
                .leftJoin(product.brand, brand)
                .fetchJoin()
                .where(brand.id.in(brandIds))
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(product.id.countDistinct())
                .from(product)
                .where(product.brand.id.in(brandIds))
                .fetchOne();

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                () -> Optional.ofNullable(total).orElse(0L)
        );
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortKey, QProduct product) {
        return switch (sortKey) {
            case "price" -> product.price.desc();
            case "createdAt" -> product.createdAt.desc();
            case "latestAt" -> product.latestAt.desc();
            case "LIKE_COUNT_DESC" -> product.likeCount.desc();
            default -> throw new IllegalArgumentException("지원하지 않는 정렬 조건: " + sortKey);
        };
    }
}
