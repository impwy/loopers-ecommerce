package com.loopers.application.product.required;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.loopers.application.brand.required.BrandRepository;
import com.loopers.application.like.required.ProductLikeRepository;
import com.loopers.application.member.required.MemberRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.like.ProductLike;
import com.loopers.domain.member.CreateMemberSpec;
import com.loopers.domain.member.Gender;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductFixture;
import com.loopers.domain.product.ProductInfo;
import com.loopers.support.stereotype.ApplicationJpaServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationJpaServiceTest
@RequiredArgsConstructor
class ProductRepositoryTest {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final MemberRepository memberRepository;
    private final ProductLikeRepository productLikeRepository;
    private final TestEntityManager entityManager;

    @Test
    void saveAndFindById() {
        Brand brand = saveBrand("브랜드");
        Product product = productRepository.save(ProductFixture.createProduct(brand));
        flushAndClear();

        Product found = productRepository.findById(product.getId()).orElseThrow();

        assertThat(found.getName()).isEqualTo(product.getName());
    }

    @Test
    void findAllBySort() {
        Brand brand = saveBrand("정렬 브랜드");
        Product older = productRepository.save(Product.create("오래된 상품", "설명", BigDecimal.TEN, brand,
                                                              ZonedDateTime.now().minusDays(1)));
        Product newer = productRepository.save(Product.create("최신 상품", "설명", BigDecimal.TEN, brand,
                                                              ZonedDateTime.now()));
        flushAndClear();

        List<Product> products = productRepository.findAll(Sort.by(Sort.Direction.DESC, "latestAt"));

        assertThat(products).extracting(Product::getId).containsExactly(newer.getId(), older.getId());
    }

    @Test
    void findByBrand() {
        Brand brand = saveBrand("대상 브랜드");
        Brand otherBrand = saveBrand("다른 브랜드");
        Product expected = productRepository.save(ProductFixture.createProduct(brand));
        productRepository.save(ProductFixture.createProduct(otherBrand));
        flushAndClear();

        List<Product> products = productRepository.findByBrand(brand);

        assertThat(products).extracting(Product::getId).containsExactly(expected.getId());
    }

    @Test
    void findByIdIn() {
        Brand brand = saveBrand("상품 브랜드");
        Product first = productRepository.save(ProductFixture.createProduct(brand));
        Product second = productRepository.save(ProductFixture.createProduct(brand));
        flushAndClear();

        List<Product> products = productRepository.findByIdIn(List.of(first.getId(), second.getId()));

        assertThat(products).extracting(Product::getId).containsExactlyInAnyOrder(first.getId(), second.getId());
    }

    @Test
    void findByIdWithBrand_fetchesBrand() {
        Brand brand = saveBrand("상품 상세 브랜드");
        Product product = productRepository.save(ProductFixture.createProduct(brand));
        flushAndClear();

        Product found = productRepository.findByIdWithBrand(product.getId()).orElseThrow();

        assertAll(
                () -> assertThat(found.getId()).isEqualTo(product.getId()),
                () -> assertThat(found.getBrand().getId()).isEqualTo(brand.getId()),
                () -> assertThat(found.getBrand().getName()).isEqualTo(brand.getName())
        );
    }

    @Test
    void findAllByIdInWithPageable() {
        Brand brand = saveBrand("상품 브랜드");
        Product first = productRepository.save(ProductFixture.createProduct(brand));
        productRepository.save(ProductFixture.createProduct(brand));
        flushAndClear();

        Page<Product> page = productRepository.findAllByIdIn(List.of(first.getId()), PageRequest.of(0, 10));

        assertAll(
                () -> assertThat(page.getContent()).extracting(Product::getId).containsExactly(first.getId()),
                () -> assertThat(page.getTotalElements()).isEqualTo(1)
        );
    }

    @Test
    void findAllByIdInWithBrand_fetchesBrand() {
        Brand brand = saveBrand("랭킹 브랜드");
        Product first = productRepository.save(ProductFixture.createProduct(brand));
        Product second = productRepository.save(ProductFixture.createProduct(brand));
        flushAndClear();

        Page<Product> page = productRepository.findAllByIdInWithBrand(List.of(first.getId(), second.getId()),
                                                                      PageRequest.of(0, 10));

        assertAll(
                () -> assertThat(page.getTotalElements()).isEqualTo(2),
                () -> assertThat(page.getContent()).extracting(Product::getId)
                                                    .containsExactlyInAnyOrder(first.getId(), second.getId()),
                () -> assertThat(page.getContent()).allSatisfy(found ->
                        assertThat(found.getBrand().getId()).isEqualTo(brand.getId()))
        );
    }

    @Test
    void findWithLikeCount() {
        Brand brand = saveBrand("좋아요 브랜드");
        Product popular = productRepository.save(ProductFixture.createProduct(brand));
        Product ordinary = productRepository.save(ProductFixture.createProduct(brand));
        Member firstMember = memberRepository.save(MemberFixture.createMember());
        Member secondMember = memberRepository.save(createMember("pwy6818", "pwy6818@loopers.app"));
        productLikeRepository.save(ProductLike.create(firstMember, popular));
        productLikeRepository.save(ProductLike.create(firstMember, ordinary));
        productLikeRepository.save(ProductLike.create(secondMember, popular));
        flushAndClear();

        Page<ProductInfo> page =
                productRepository.findWithLikeCount("LIKE_COUNT_DESC", List.of(brand.getId()), PageRequest.of(0, 10));

        assertAll(
                () -> assertThat(page.getTotalElements()).isEqualTo(2),
                () -> assertThat(page.getContent().get(0).productId()).isEqualTo(popular.getId()),
                () -> assertThat(page.getContent().get(0).brandId()).isEqualTo(brand.getId()),
                () -> assertThat(page.getContent().get(0).brandName()).isEqualTo(brand.getName()),
                () -> assertThat(page.getContent().get(0).likeCount()).isEqualTo(2L)
        );
    }

    @Test
    void findByBrandDenormalizationWithLike() {
        Brand brand = saveBrand("비정규화 브랜드");
        Product product = productRepository.save(ProductFixture.createProduct(brand));
        product.increaseLikeCount();
        product.increaseLikeCount();
        flushAndClear();

        Page<ProductInfo> page =
                productRepository.findByBrandDenormalizationWithLike("LIKE_COUNT_DESC", List.of(brand.getId()),
                                                                      PageRequest.of(0, 10));

        assertAll(
                () -> assertThat(page.getContent().get(0).productId()).isEqualTo(product.getId()),
                () -> assertThat(page.getContent().get(0).brandId()).isEqualTo(brand.getId()),
                () -> assertThat(page.getContent().get(0).brandName()).isEqualTo(brand.getName()),
                () -> assertThat(page.getContent().get(0).likeCount()).isEqualTo(2L)
        );
    }

    @Test
    void findByIdPessimisticLock() {
        Brand brand = saveBrand("잠금 브랜드");
        Product product = productRepository.save(ProductFixture.createProduct(brand));
        flushAndClear();

        Product found = productRepository.findByIdPessimisticLock(product.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(product.getId());
    }

    @Test
    void findAllByOrderByLikeCountDesc() {
        Brand brand = saveBrand("좋아요 정렬 브랜드");
        Product popular = productRepository.save(ProductFixture.createProduct(brand));
        Product ordinary = productRepository.save(ProductFixture.createProduct(brand));
        popular.increaseLikeCount();
        popular.increaseLikeCount();
        ordinary.increaseLikeCount();
        flushAndClear();

        Page<Product> page = productRepository.findAllByOrderByLikeCountDesc(PageRequest.of(0, 10));

        assertThat(page.getContent()).extracting(Product::getId).containsExactly(popular.getId(), ordinary.getId());
    }

    @Test
    void findAllByOrderByLikeCountDescWithBrand_fetchesBrand() {
        Brand brand = saveBrand("좋아요 상세 브랜드");
        Product product = productRepository.save(ProductFixture.createProduct(brand));
        flushAndClear();

        Page<Product> page = productRepository.findAllByOrderByLikeCountDescWithBrand(PageRequest.of(0, 10));

        Product found = page.getContent().stream()
                            .filter(candidate -> candidate.getId().equals(product.getId()))
                            .findFirst()
                            .orElseThrow();
        assertThat(found.getBrand().getName()).isEqualTo(brand.getName());
    }

    private Brand saveBrand(String name) {
        return brandRepository.save(Brand.create(name, "브랜드 설명", LocalDate.of(2001, 1, 1)));
    }

    private Member createMember(String memberId, String email) {
        return Member.create(new CreateMemberSpec(memberId, "secret", Gender.MALE, email,
                                                   LocalDate.of(2025, 7, 13)));
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
