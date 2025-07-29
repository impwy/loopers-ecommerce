package com.loopers.application;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.application.provided.MemberFinder;
import com.loopers.application.provided.ProductFinder;
import com.loopers.application.provided.ProductLikeFinder;
import com.loopers.application.provided.ProductLikeRegister;
import com.loopers.domain.like.ProductLike;
import com.loopers.domain.member.Member;
import com.loopers.domain.product.Product;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductLikeFacade {
    private final ProductLikeRegister productLikeRegister;
    private final ProductLikeFinder productLikeFinder;
    private final MemberFinder memberFinder;
    private final ProductFinder productFinder;

    @Transactional
    public ProductLike create(Long memberId, Long productId) {
        productLikeFinder.throwConflictExceptionHasLike(memberId, productId);

        Member member = memberFinder.find(memberId);
        Product product = productFinder.find(productId);
        ProductLike productLike = ProductLike.create(member, product);
        return productLikeRegister.create(productLike);
    }

    @Transactional
    public ProductLike delete(Long memberId, Long productId) {
        ProductLike productLike = productLikeFinder.find(memberId, productId);
        productLike.delete();
        return productLike;
    }
}
