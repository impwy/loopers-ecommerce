package com.loopers.domain.like;

import static jakarta.persistence.FetchType.LAZY;
import static java.util.Objects.requireNonNull;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.member.Member;
import com.loopers.domain.product.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "product_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductLike extends BaseEntity {

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = LAZY)
    private Member member;

    @JoinColumn(name = "product_id")
    @ManyToOne(fetch = LAZY)
    private Product product;

    private ProductLike(Member member, Product product) {
        this.member = requireNonNull(member);
        this.product = requireNonNull(product);
    }

    public static ProductLike create(Member member, Product product) {
        if (member == null || product == null) {
            throw new IllegalArgumentException("유저와 상품은 필수 항목입니다.");
        }
        return new ProductLike(member, product);
    }
}
