package com.loopers.domain.like;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductFixture;

class ProductLikeTest {

    @DisplayName("유저가 없을 때 상품 좋아요 생성 실패 테스트")
    @Test
    void create_fail_productlike_when_member_is_null() {
        Product product = ProductFixture.createProduct();
        ReflectionTestUtils.setField(product, "id", 1L);

        assertThatThrownBy(() -> ProductLike.create(null, product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품이 없을 때 상품 좋아요 생성 실패 테스트")
    @Test
    void create_fail_productlike_when_product_is_null() {
        Member member = MemberFixture.createMember();
        ReflectionTestUtils.setField(member, "id", 1L);

        assertThatThrownBy(() -> ProductLike.create(member, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 좋아요 생성 테스트")
    @Test
    void create_productlike_test() {
        Member member = MemberFixture.createMember();
        ReflectionTestUtils.setField(member, "id", 1L);
        Product product = ProductFixture.createProduct();
        ReflectionTestUtils.setField(product, "id", 1L);

        ProductLike productLike = ProductLike.create(member, product);

        assertAll(
                () -> assertThat(productLike.getMember().getId()).isEqualTo(member.getId()),
                () -> assertThat(productLike.getProduct().getId()).isEqualTo(product.getId()),
                () -> assertThat(productLike.getProduct().getPrice()).isEqualTo(product.getPrice())
        );
    }

    /**
     * deleted_at이 존재하면 삭제된 것이다.
     */
    @DisplayName("상품 좋아요 취소 테스트")
    @Test
    void cancel_productlike_test() {
        Member member = MemberFixture.createMember();
        ReflectionTestUtils.setField(member, "id", 1L);
        Product product = ProductFixture.createProduct();
        ReflectionTestUtils.setField(product, "id", 1L);

        ProductLike productLike = ProductLike.create(member, product);

        productLike.delete();

        assertThat(productLike.getDeletedAt()).isNotNull();
    }

    @DisplayName("취소한 상품 좋아요 다시 좋아요 테스트")
    @Test
    void canceled_productlike_like_test() {
        Member member = MemberFixture.createMember();
        ReflectionTestUtils.setField(member, "id", 1L);
        Product product = ProductFixture.createProduct();
        ReflectionTestUtils.setField(product, "id", 1L);

        ProductLike productLike = ProductLike.create(member, product);

        // 삭제
        productLike.delete();
        assertThat(productLike.getDeletedAt()).isNotNull();

        //생성
        productLike.restore();
        assertAll(
                () -> assertThat(productLike.getDeletedAt()).isNull(),
                () -> assertThat(productLike.getMember().getMemberId().memberId()).isEqualTo(member.getMemberId().memberId()),
                () -> assertThat(productLike.getProduct().getName()).isEqualTo(product.getName())
        );
    }
}
