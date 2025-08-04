package com.loopers.domain.order;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.order.orderitem.OrderItem;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    private Long memberId;

    @Embedded
    private OrderNo orderNo;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private final List<OrderItem> orderItems = new ArrayList<>();

    private Order(Long memberId) {
        this.memberId = requireNonNull(memberId);
        this.orderNo = OrderNo.newOrderNo();
    }

    public static Order create(CreateOrderSpec createSpec) {
        if (createSpec.memberId() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자는 필수입니다.");
        }
        return new Order(createSpec.memberId());
    }

    public void addOrderItem(Long productId, Long quantity, BigDecimal totalPrice) {
        OrderItem orderItem = OrderItem.create(this, productId, quantity, totalPrice);
        this.orderItems.add(orderItem);
    }
}
