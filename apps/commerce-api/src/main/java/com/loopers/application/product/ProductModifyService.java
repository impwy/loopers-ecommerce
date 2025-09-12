package com.loopers.application.product;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.ProductFinder;
import com.loopers.application.provided.ProductRegister;
import com.loopers.application.required.InMemoryRepository;
import com.loopers.application.required.ProductRepository;
import com.loopers.domain.product.CreateProductSpec;
import com.loopers.domain.product.Product;

import lombok.RequiredArgsConstructor;

@Service
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
