package com.loopers.adapter.integration.jpa.product;

import static com.loopers.domain.brand.QBrand.brand;
import static com.loopers.domain.like.QProductLike.productLike;
import static com.loopers.domain.product.QProduct.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.QProduct;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {
    private final JPQLQueryFactory queryFactory;

    @Override
    public Page<ProductInfo> findWithLikeCount(String sortKey, List<Long> brandIds, Pageable pageable) {
        List<ProductInfo> content = queryFactory
                .select(Projections.constructor(ProductInfo.class,
                                                product.id,
                                                brand.id,
                                                product.name,
                                                product.description,
                                                product.price,
                                                brand.name,
                                                product.createdAt,
                                                product.updatedAt,
                                                productLike.countDistinct()
                ))
                .from(product)
                .join(product.brand, brand)
                .leftJoin(productLike).on(productLike.product.eq(product))
                .where(brand.id.in(brandIds))
                .groupBy(product.id, brand.id, product.name, product.description, product.price,
                         brand.name, product.createdAt, product.updatedAt)
                .orderBy(getOrderSpecifier(sortKey, product))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                () -> Optional.ofNullable(queryFactory
                                                  .select(product.id.countDistinct())
                                                  .from(product)
                                                  .where(product.brand.id.in(brandIds))
                                                  .fetchOne()
                ).orElse(0L)
        );
    }

    @Override
    public Page<ProductInfo> findByBrandDenormalizationWithLike(String sortKey, List<Long> brandIds, Pageable pageable) {
        List<ProductInfo> content = queryFactory
                .select(Projections.constructor(ProductInfo.class,
                                                product.id,
                                                brand.id,
                                                product.name,
                                                product.description,
                                                product.price,
                                                brand.name,
                                                product.createdAt,
                                                product.updatedAt,
                                                product.likeCount
                ))
                .from(product)
                .leftJoin(product.brand, brand)
                .where(brand.id.in(brandIds))
                .orderBy(getOrderSpecifier(sortKey, product))
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
