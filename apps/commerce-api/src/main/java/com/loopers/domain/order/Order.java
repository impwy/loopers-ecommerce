package com.loopers.domain.order;

import static java.util.Objects.requireNonNull;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import jakarta.persistence.Embedded;
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

    @Embedded
    private Address address;

    private Order(Long memberId, String orderNo, Address address) {
        this.memberId = requireNonNull(memberId);
        this.orderNo = requireNonNull(orderNo);
        this.address = requireNonNull(address);
    }

    public static Order create(CreateOrderSpec createSpec) {
        if (createSpec.memberId() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자는 필수입니다.");
        }
        if (createSpec.orderNo() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문번호는 필수입니다.");
        }
        if (createSpec.address() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주소는 필수입니다.");
        }
        return new Order(createSpec.memberId(), createSpec.orderNo(), createSpec.address());
    }
}
