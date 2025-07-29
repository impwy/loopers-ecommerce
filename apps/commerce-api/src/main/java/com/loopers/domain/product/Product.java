package com.loopers.domain.product;

import java.math.BigDecimal;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.brand.Brand;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
    private String name;
    private String description;
    private BigDecimal price;

    @JoinColumn(name = "brand_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Brand brand;

    private Product(String name, String description, BigDecimal price, Brand brand) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.brand = brand;
    }

    public static Product create(String name, String description, BigDecimal price, Brand brand) {
        return new Product(name, description, price, brand);
    }
}

