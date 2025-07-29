package com.loopers.application.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.ProductLikeFacade;
import com.loopers.application.required.MemberRepository;
import com.loopers.application.required.ProductRepository;
import com.loopers.domain.like.ProductLike;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.product.Product;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest
class ProductLikeRegisterIntegrationTest {

    @Autowired
    private ProductLikeFacade productLikeFacade;

    @MockitoSpyBean
    private MemberRepository memberRepository;

    @MockitoSpyBean
    private ProductRepository productRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("상품 좋아요 테스트")
    @Test
    void create_productlike_test() {
        Member member = memberRepository.save(MemberFixture.createMember());
        Product product = productRepository.save(Product.create("상품", "상품입니다.", BigDecimal.valueOf(500)));

        ProductLike productLike = productLikeFacade.create(member.getId(), product.getId());

        assertAll(
                () -> assertThat(productLike.getMember().getPoint().getAmount().compareTo(member.getPoint().getAmount())).isZero(),
                () -> assertThat(productLike.getProduct().getPrice().compareTo(product.getPrice())).isZero(),
                () -> assertThat(productLike.getMember().getMemberId().memberId()).isEqualTo(member.getMemberId().memberId()),
                () -> assertThat(productLike.getProduct().getName()).isEqualTo(product.getName())
        );
    }
}
