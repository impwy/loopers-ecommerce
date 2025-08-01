package com.loopers.application.like;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.ProductLikeRegister;
import com.loopers.application.required.ProductLikeRepository;
import com.loopers.domain.like.ProductLike;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductLikeModifyService implements ProductLikeRegister {

    private final ProductLikeRepository productLikeRepository;

    @Override
    public ProductLike create(ProductLike productLike) {
        return productLikeRepository.save(productLike);
    }
}
