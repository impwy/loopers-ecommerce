package com.loopers.application.like.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.application.like.ProductLikeFacade;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.like.ProductLike;
import com.loopers.domain.member.Member;
import com.loopers.domain.product.Product;
import com.loopers.shared.error.CoreException;
import com.loopers.shared.error.ErrorType;
import com.loopers.support.BaseApplicationServiceTest;
import com.loopers.support.stereotype.ApplicationServiceTest;

@ApplicationServiceTest
class ProductLikeFacadeTest extends BaseApplicationServiceTest {

    @Autowired
    private ProductLikeFacade productLikeFacade;

    Product product;

    @BeforeEach
    void setUp() {
        Brand brand = prepareBrand();
        product = prepareProduct(brand);
    }

    @DisplayName("상품 좋아요 멱등성 테스트")
    @Test
    void double_create_fail_productlike_test() {
        Member member = prepareMember();

        // first create like
        productLikeFacade.create(member.getId(), product.getId());

        //second create like
        CoreException exception = assertThrows(CoreException.class, () -> productLikeFacade.create(member.getId(), product.getId()));

        assertThat(exception.getErrorType()).isEqualTo(ErrorType.CONFLICT);
    }

    @DisplayName("상품 좋아요 테스트")
    @Test
    @Transactional
    void create_productlike_test() {
        Member member = prepareMember();

        ProductLike productLike = productLikeFacade.create(member.getId(), product.getId());

        assertAll(
                () -> assertThat(productLike.getMember().getPoint().compareTo(member.getPoint())).isZero(),
                () -> assertThat(productLike.getProduct().getPrice().compareTo(product.getPrice())).isZero(),
                () -> assertThat(productLike.getMember().getUserId().value()).isEqualTo(member.getUserId().value()),
                () -> assertThat(productLike.getProduct().getName()).isEqualTo(product.getName())
        );
    }

    @DisplayName("상품 좋아요 취소 테스트")
    @Test
    void cancel_productlike_test() {
        Member member = prepareMember();

        prepareProductLike(member, product);

        ProductLike productLike = productLikeFacade.delete(member.getId(), product.getId());

        assertThat(productLike.getDeletedAt()).isNotNull();
    }

    @DisplayName("상품 좋아요 취소 후 생성 테스트")
    @Test
    void canceled_productlike_then_create_test() {
        Member member = prepareMember();

        ProductLike savedProductLike = prepareProductLike(member, product);

        ProductLike productLike = productLikeFacade.delete(member.getId(), product.getId());
        assertThat(productLike.getDeletedAt()).isNotNull();

        ProductLike recreatedProductLike = productLikeFacade.create(member.getId(), product.getId());

        assertAll(
                () -> assertThat(recreatedProductLike.getId()).isEqualTo(savedProductLike.getId()),
                () -> assertThat(recreatedProductLike.getDeletedAt()).isNull()
        );
    }
}
