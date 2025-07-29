package com.loopers.infrastructure.like;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.like.ProductLike;
import com.loopers.domain.member.Member;

public interface ProductLikeJpaRepository extends JpaRepository<ProductLike, Long> {
    List<ProductLike> member(Member member);

    Optional<ProductLike> findByMemberIdAndProductId(Long memberId, Long productId);
}
