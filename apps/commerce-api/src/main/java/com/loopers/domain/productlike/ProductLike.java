package com.loopers.domain.productlike;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.member.Member;
import com.loopers.domain.product.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@Table(name = "product_like")
@RequiredArgsConstructor
public class ProductLike extends BaseEntity {

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "product_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private ProductLike(Member member, Product product) {
        this.member = member;
        this.product = product;
    }

    public static ProductLike create(Member member, Product product) {
        return new ProductLike(member, product);
    }
}
