package com.loopers.application.product;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.ProductRegister;
import com.loopers.application.required.ProductRepository;
import com.loopers.domain.product.Product;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductModifyService implements ProductRegister {
    private final ProductRepository productRepository;

    @Override
    public Product register(Product product) {
        return productRepository.save(product);
    }
}
