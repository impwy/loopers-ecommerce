package com.loopers.domain.order;

import static java.util.Objects.requireNonNull;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    private Long memberId;

    private String orderNo;

    private Order(Long memberId, String orderNo) {
        this.memberId = requireNonNull(memberId);
        this.orderNo = requireNonNull(orderNo);
    }

    public static Order create(CreateOrderSpec createSpec) {
        if (createSpec.memberId() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자는 필수입니다.");
        }
        if (createSpec.orderNo() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문번호는 필수입니다.");
        }
        return new Order(createSpec.memberId(), createSpec.orderNo());
    }
}
