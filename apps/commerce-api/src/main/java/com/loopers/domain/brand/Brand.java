package com.loopers.domain.brand;

import com.loopers.domain.BaseEntity;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Brand extends BaseEntity {
    private String name;
    private String description;

    private Brand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static Brand create(String name, String description) {
        return new Brand(name, description);
    }
}
