package com.loopers.infrastructure.redis;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import lombok.Data;

@Data
public class CachedPage<T> {
    private List<T> content;
    private Long totalElements;

    public static <T> CachedPage<T> of(Page<T> page) {
        CachedPage<T> cp = new CachedPage<>();
        cp.content = page.getContent();
        cp.totalElements = page.getTotalElements();
        return cp;
    }

    public Page<T> toPage(Pageable pageable) {
        return new PageImpl<>(content, pageable, totalElements);
    }
}
