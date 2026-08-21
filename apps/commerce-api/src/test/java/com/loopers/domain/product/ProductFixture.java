package com.loopers.domain.product;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import com.loopers.domain.brand.Brand;

import org.instancio.Instancio;

import static org.instancio.Select.field;

public class ProductFixture {
    public static Product createProduct(Brand brand) {
        return Instancio.of(Product.class)
                        .ignore(field(Product::getId))
                        .generate(field(Product::getName), gen -> gen.string().minLength(1).maxLength(20))
                        .generate(field(Product::getDescription), gen -> gen.string().minLength(1).maxLength(50))
                        .generate(field(Product::getPrice), gen -> gen.math().bigDecimal()
                                .range(BigDecimal.ONE, BigDecimal.valueOf(10_000)).scale(0))
                        .set(field(Product::getBrand), brand)
                        .set(field(Product::getLatestAt), ZonedDateTime.now())
                        .set(field(Product::getLikeCount), 0L)
                        .create();
    }

    public static Product createProduct(String name, String description, BigDecimal price,
                                        Brand brand, ZonedDateTime latestAt) {
        return Instancio.of(Product.class)
                        .ignore(field(Product::getId))
                        .set(field(Product::getName), name)
                        .set(field(Product::getDescription), description)
                        .set(field(Product::getPrice), price)
                        .set(field(Product::getBrand), brand)
                        .set(field(Product::getLatestAt), latestAt)
                        .set(field(Product::getLikeCount), 0L)
                        .create();
    }

    public static CreateProductSpec createProductSpec(Brand brand) {
        return Instancio.of(CreateProductSpec.class)
                        .generate(field(CreateProductSpec::name), gen -> gen.string().minLength(1).maxLength(20))
                        .generate(field(CreateProductSpec::description), gen -> gen.string().minLength(1).maxLength(50))
                        .generate(field(CreateProductSpec::price), gen -> gen.math().bigDecimal()
                                .range(BigDecimal.ONE, BigDecimal.valueOf(10_000)).scale(0))
                        .set(field(CreateProductSpec::brand), brand)
                        .set(field(CreateProductSpec::latestAt), ZonedDateTime.now())
                        .create();
    }

    public static CreateProductSpec createProductSpec(String name, String description, BigDecimal price,
                                                      Brand brand, ZonedDateTime latestAt) {
        return Instancio.of(CreateProductSpec.class)
                        .set(field(CreateProductSpec::name), name)
                        .set(field(CreateProductSpec::description), description)
                        .set(field(CreateProductSpec::price), price)
                        .set(field(CreateProductSpec::brand), brand)
                        .set(field(CreateProductSpec::latestAt), latestAt)
                        .create();
    }
}
