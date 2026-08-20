package com.loopers.application.like;

import com.loopers.application.like.provided.ProductLikeRegister;
import com.loopers.application.like.required.ProductLikeRepository;
import com.loopers.domain.like.ProductLike;
import com.loopers.shared.stereotype.ApplicationValidService;

import lombok.RequiredArgsConstructor;

@ApplicationValidService
@RequiredArgsConstructor
public class ProductLikeModifyService implements ProductLikeRegister {

    private final ProductLikeRepository productLikeRepository;

    @Override
    public ProductLike create(ProductLike productLike) {
        return productLikeRepository.save(productLike);
    }
}
