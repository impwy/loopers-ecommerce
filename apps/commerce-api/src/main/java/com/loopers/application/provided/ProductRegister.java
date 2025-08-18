package com.loopers.application.provided;

import com.loopers.domain.product.CreateProductSpec;
import com.loopers.domain.product.Product;

public interface ProductRegister {
    Product register(CreateProductSpec createProductSpec);

    Product increaseLike(Long product);

    Product decreaseLike(Long product);
}
