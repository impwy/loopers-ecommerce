package com.loopers.domain.like;

import com.loopers.domain.member.Member;
import com.loopers.domain.product.Product;

import org.instancio.Instancio;

import static org.instancio.Select.field;

public final class ProductLikeFixture {
    private ProductLikeFixture() {
    }

    public static ProductLike createProductLike(Member member, Product product) {
        return Instancio.of(ProductLike.class)
                        .ignore(field(ProductLike::getId))
                        .set(field(ProductLike::getMember), member)
                        .set(field(ProductLike::getProduct), product)
                        .create();
    }
}
