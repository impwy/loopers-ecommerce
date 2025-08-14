package com.loopers.application.product;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.ProductFinder;
import com.loopers.application.provided.ProductRegister;
import com.loopers.application.required.ProductRepository;
import com.loopers.domain.product.CreateProductSpec;
import com.loopers.domain.product.Product;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductModifyService implements ProductRegister {
    private final ProductRepository productRepository;
    private final ProductFinder productFinder;

    @Override
    public Product register(CreateProductSpec createProductSpec) {
        Product product = createProductSpec.toEntity();
        return productRepository.save(product);
    }

    @Override
    public Product increaseLike(Long productId) {
        Product product = productFinder.findProductPessimisticLock(productId);

        product.increaseLikeCount();
        return productRepository.save(product);
    }

    @Override
    public Product decreaseLike(Long productId) {
        Product product = productFinder.findProductPessimisticLock(productId);

        product.decreaseLikeCount();
        return productRepository.save(product);
    }
}
