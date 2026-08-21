package com.loopers.application.like.required;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.like.ProductLike;
import com.loopers.domain.like.ProductLikeFixture;
import com.loopers.domain.member.Member;
import com.loopers.domain.product.Product;
import com.loopers.support.BaseRepositoryTest;
import com.loopers.support.stereotype.ApplicationJpaServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationJpaServiceTest
@RequiredArgsConstructor
class ProductLikeRepositoryTest extends BaseRepositoryTest {
    final ProductLikeRepository productLikeRepository;

    @Test
    void saveFindByMemberIdAndProductIdAndCountByProductId() {
        Brand brand = prepareBrand();
        Member member = prepareMember();
        Product product = prepareProduct(brand);

        ProductLike productLike = productLikeRepository.save(ProductLikeFixture.createProductLike(member, product));
        Long memberId = member.getId();
        Long productId = product.getId();
        flushAndClear();

        ProductLike found = productLikeRepository.findByMemberIdAndProductId(memberId, productId).orElseThrow();
        Long count = productLikeRepository.countByProductId(productId);

        assertThat(found.getId()).isEqualTo(productLike.getId());
        assertThat(found.getMember().getId()).isEqualTo(memberId);
        assertThat(found.getProduct().getId()).isEqualTo(productId);
        assertThat(count).isEqualTo(1L);
    }
}
