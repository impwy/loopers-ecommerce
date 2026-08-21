package com.loopers.application.product;

import com.loopers.application.inmemory.required.InMemoryRepository;
import com.loopers.application.product.provided.ProductFinder;
import com.loopers.application.product.provided.ProductRegister;
import com.loopers.application.product.required.ProductRepository;
import com.loopers.domain.product.CreateProductSpec;
import com.loopers.domain.product.Product;
import com.loopers.shared.stereotype.ApplicationValidService;

import lombok.RequiredArgsConstructor;

@ApplicationValidService
@RequiredArgsConstructor
public class ProductModifyService implements ProductRegister {
    private final ProductRepository productRepository;
    private final ProductFinder productFinder;
    private final InMemoryRepository inMemoryRepository;

    @Override
    public Product register(CreateProductSpec createProductSpec) {
        Product product = createProductSpec.toEntity();
        return productRepository.save(product);
    }

    @Override
    public Product increaseLike(Long productId) {
        Product product = productFinder.findProductPessimisticLock(productId);
        product.increaseLikeCount();
        Product savedProduct = productRepository.save(product);

        String key = "product:like:" + product.getId();
        inMemoryRepository.save(key, product.getLikeCount());
        return savedProduct;
    }

    @Override
    public Product decreaseLike(Long productId) {
        Product product = productFinder.findProductPessimisticLock(productId);

        product.decreaseLikeCount();
        return productRepository.save(product);
    }
}
