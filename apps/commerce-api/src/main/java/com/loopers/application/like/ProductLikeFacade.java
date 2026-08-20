package com.loopers.application.like;

import org.springframework.transaction.annotation.Transactional;

import com.loopers.application.like.provided.ProductLikeFinder;
import com.loopers.application.like.provided.ProductLikeRegister;
import com.loopers.application.member.provided.MemberFinder;
import com.loopers.application.product.provided.ProductFinder;
import com.loopers.domain.like.ProductLike;
import com.loopers.domain.member.Member;
import com.loopers.domain.product.Product;
import com.loopers.shared.stereotype.ApplicationValidService;

import lombok.RequiredArgsConstructor;

@ApplicationValidService
@RequiredArgsConstructor
public class ProductLikeFacade {
    private final ProductLikeRegister productLikeRegister;
    private final ProductLikeFinder productLikeFinder;
    private final MemberFinder memberFinder;
    private final ProductFinder productFinder;

    @Transactional
    public ProductLike create(Long memberId, Long productId) {
        var restoredProductLike = productLikeFinder.restoreDeletedOrThrowConflict(memberId, productId);
        if (restoredProductLike.isPresent()) {
            return restoredProductLike.get();
        }

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
