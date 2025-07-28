package com.loopers.domain.productlike;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.product.Product;

class LikeTest {

    @DisplayName("좋아요 생성 테스트")
    @Test
    void create_like_test() {
        Member member = MemberFixture.createMember();
        ReflectionTestUtils.setField(member, "id", 1L);
        Product product = Product.create("상품", "상품입니다.", BigDecimal.valueOf(500));
        ReflectionTestUtils.setField(product, "id", 1L);

        ProductLike like = ProductLike.create(member, product);

        assertThat(like).isNotNull();
        assertAll(
                () -> assertThat(like.getMember().getId()).isEqualTo(1L),
                () -> assertThat(like.getProduct().getId()).isEqualTo(1L),
                () -> assertThat(like.getMember().getMemberId()).isEqualTo(member.getMemberId()),
                () -> assertThat(like.getProduct().getName()).isEqualTo(product.getName())
        );
    }
}
