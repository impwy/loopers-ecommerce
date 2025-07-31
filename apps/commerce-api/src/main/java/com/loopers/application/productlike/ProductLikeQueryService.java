package com.loopers.application.productlike;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.ProductLikeFinder;
import com.loopers.application.required.ProductLikeRepository;
import com.loopers.domain.like.ProductLike;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductLikeQueryService implements ProductLikeFinder {
    private final ProductLikeRepository productLikeRepository;

    @Override
    public ProductLike find(Long memberId, Long productId) {
        return productLikeRepository.findByMemberIdAndProductId(memberId, productId)
                                    .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "좋아요를 찾을 수 없습니다."));
    }

    @Override
    public void throwConflictExceptionHasLike(Long memberId, Long productId) {
        productLikeRepository.findByMemberIdAndProductId(memberId, productId)
                             .ifPresent(productLike -> {
                                 if (productLike.isNotDeleted(productLike)) {
                                     throw new CoreException(ErrorType.CONFLICT, "좋아요가 중복 되었습니다.");
                                 } else {
                                     productLike.restore();
                                 }
                             });
    }

    @Override
    public Long countByProductId(Long productId) {
        return productLikeRepository.countByProductId(productId);
    }
}
