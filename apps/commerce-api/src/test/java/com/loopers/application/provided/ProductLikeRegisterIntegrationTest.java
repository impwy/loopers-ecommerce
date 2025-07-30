package com.loopers.application.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.ProductLikeFacade;
import com.loopers.application.required.BrandRepository;
import com.loopers.application.required.MemberRepository;
import com.loopers.application.required.ProductLikeRepository;
import com.loopers.application.required.ProductRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandFixture;

import com.loopers.domain.like.ProductLike;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductFixture;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest
class ProductLikeRegisterIntegrationTest {

    @Autowired
    private ProductLikeFacade productLikeFacade;

    @MockitoSpyBean
    private ProductLikeRepository productLikeRepository;

    @MockitoSpyBean
    private MemberRepository memberRepository;

    @MockitoSpyBean
    private ProductRepository productRepository;

    @MockitoSpyBean
    private BrandRepository brandRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    Product product;

    @BeforeEach
    void setUp() {
        Brand brand = brandRepository.create(BrandFixture.createBrand());
        product = productRepository.save(ProductFixture.createProduct(brand));
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("상품 좋아요 멱등성 테스트")
    @Test
    void double_create_fail_productlike_test() {
        Member member = memberRepository.save(MemberFixture.createMember());

        // first create like
        productLikeFacade.create(member.getId(), product.getId());

        //second create like
        CoreException exception = assertThrows(CoreException.class, () -> productLikeFacade.create(member.getId(), product.getId()));

        assertThat(exception.getErrorType()).isEqualTo(ErrorType.CONFLICT);
    }

    @DisplayName("상품 좋아요 테스트")
    @Test
    void create_productlike_test() {
        Member member = memberRepository.save(MemberFixture.createMember());

        ProductLike productLike = productLikeFacade.create(member.getId(), product.getId());

        assertAll(
                () -> assertThat(productLike.getMember().getPoint().getAmount().compareTo(member.getPoint().getAmount())).isZero(),
                () -> assertThat(productLike.getProduct().getPrice().compareTo(product.getPrice())).isZero(),
                () -> assertThat(productLike.getMember().getMemberId().memberId()).isEqualTo(member.getMemberId().memberId()),
                () -> assertThat(productLike.getProduct().getName()).isEqualTo(product.getName())
        );
    }

    @DisplayName("상품 좋아요 취소 테스트")
    @Test
    void cancel_productlike_test() {
        Member member = memberRepository.save(MemberFixture.createMember());

        productLikeRepository.save(ProductLike.create(member, product));

        ProductLike productLike = productLikeFacade.delete(member.getId(), product.getId());

        assertThat(productLike.getDeletedAt()).isNotNull();
    }

    @DisplayName("상품 좋아요 취소 후 생성 테스트")
    @Test
    void canceled_productlike_then_create_test() {
        Member member = memberRepository.save(MemberFixture.createMember());

        productLikeRepository.save(ProductLike.create(member, product));

        ProductLike productLike = productLikeFacade.delete(member.getId(), product.getId());
        assertThat(productLike.getDeletedAt()).isNotNull();

        productLike.restore();
        assertThat(productLike.getDeletedAt()).isNull();
    }
}
