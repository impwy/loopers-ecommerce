package com.loopers.domain.brand;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 브랜드의 상세 정보를 표현하는 값 객체입니다.
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrandProfile {
    @Column(name = "since", nullable = false)
    private LocalDate since;

    private BrandProfile(LocalDate since) {
        this.since = since;
    }

    public static BrandProfile create(LocalDate since) {
        return new BrandProfile(since);
    }
}
