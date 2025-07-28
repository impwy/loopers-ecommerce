package com.loopers.domain.product;

import java.math.BigDecimal;

import com.loopers.domain.BaseEntity;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
    private String name;
    private String description;
    private BigDecimal price;

    private Product(String name, String description, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public static Product create(String name, String description, BigDecimal price) {
        return new Product(name, description, price);
    }
}

