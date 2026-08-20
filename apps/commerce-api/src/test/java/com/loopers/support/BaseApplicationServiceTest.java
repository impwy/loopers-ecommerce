package com.loopers.support;

import org.springframework.beans.factory.annotation.Autowired;

import com.loopers.application.brand.provided.BrandRegister;
import com.loopers.application.product.provided.ProductRegister;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandFixture;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductFixture;
import com.loopers.support.stereotype.ApplicationServiceTest;

@ApplicationServiceTest
public class BaseApplicationServiceTest {
    @Autowired
    BrandRegister brandRegister;

    @Autowired
    ProductRegister productRegister;

    protected Brand brand;
    protected Product product;

    protected Brand prepareBrand() {
        brand = brandRegister.create(BrandFixture.createBrand());

        return brand;
    }

    protected Product prepareProduct() {
        product = productRegister.register(ProductFixture.createProductSpec(brand));
        return product;
    }
}
