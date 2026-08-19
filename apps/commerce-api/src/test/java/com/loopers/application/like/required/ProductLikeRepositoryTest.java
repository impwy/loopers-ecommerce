package com.loopers.application.like.required;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandFixture;
import com.loopers.domain.like.ProductLike;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductFixture;
import com.loopers.support.stereotype.ApplicationJpaServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationJpaServiceTest
@RequiredArgsConstructor
class ProductLikeRepositoryTest {
    final ProductLikeRepository productLikeRepository;
    final EntityManager entityManager;

    @Test
    void saveFindByMemberIdAndProductIdAndCountByProductId() {
        Brand brand = BrandFixture.createBrand();
        Member member = MemberFixture.createMember();
        Product product = ProductFixture.createProduct(brand);
        entityManager.persist(brand);
        entityManager.persist(member);
        entityManager.persist(product);
        entityManager.flush();

        ProductLike productLike = productLikeRepository.save(ProductLike.create(member, product));
        Long memberId = member.getId();
        Long productId = product.getId();
        entityManager.flush();
        entityManager.clear();

        ProductLike found = productLikeRepository.findByMemberIdAndProductId(memberId, productId).orElseThrow();
        Long count = productLikeRepository.countByProductId(productId);

        assertThat(found.getId()).isEqualTo(productLike.getId());
        assertThat(found.getMember().getId()).isEqualTo(memberId);
        assertThat(found.getProduct().getId()).isEqualTo(productId);
        assertThat(count).isEqualTo(1L);
    }
}
