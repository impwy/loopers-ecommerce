package com.loopers.domain.brand;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;

import com.loopers.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@Table(name = "brand")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Brand extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Embedded
    private BrandProfile brandProfile;

    public static Brand create(String name, String description, LocalDate since) {
        requireNonNull(name, "브랜드 이름은 필수입니다.");
        requireNonNull(since, "브랜드 설립일은 필수입니다.");
        Brand brand = new Brand();
        brand.name = name;
        brand.description = description;
        brand.brandProfile = BrandProfile.create(since);
        return brand;
    }
}
