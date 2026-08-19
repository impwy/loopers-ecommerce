package com.loopers.application.like;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.loopers.application.product.provided.ProductLikeFinder;
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
    public Optional<ProductLike> restoreDeletedOrThrowConflict(Long memberId, Long productId) {
        Optional<ProductLike> productLikeOpt = productLikeRepository.findByMemberIdAndProductId(memberId, productId);
        if (productLikeOpt.isEmpty()) {
            return Optional.empty();
        }

        ProductLike productLike = productLikeOpt.get();
        if (productLike.isNotDeleted(productLike)) {
            throw new CoreException(ErrorType.CONFLICT, "좋아요가 중복 되었습니다.");
        }

        productLike.restore();
        return Optional.of(productLike);
    }

    @Override
    public Long countByProductId(Long productId) {
        return productLikeRepository.countByProductId(productId);
    }
}
