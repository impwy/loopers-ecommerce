package com.loopers.domain.order;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.order.orderitem.CreateOrderItemSpec;
import com.loopers.domain.order.orderitem.OrderItem;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Column(name = "member_id")
    private Long memberId;

    @Embedded
    private OrderNo orderNo;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private final List<OrderItem> orderItems = new ArrayList<>();

    private Order(Long memberId) {
        this.memberId = requireNonNull(memberId);
        this.orderStatus = OrderStatus.PENDING;
        this.orderNo = OrderNo.newOrderNo();
    }

    public static Order create(CreateOrderSpec createSpec) {
        if (createSpec.memberId() == null) {
            throw new IllegalArgumentException("사용자는 필수입니다.");
        }
        return new Order(createSpec.memberId());
    }

    public void addOrderItem(Long productId, Long quantity, Long couponId) {
        OrderItem orderItem = OrderItem.create(this, productId, quantity, couponId);
        this.orderItems.add(orderItem);
    }

    public Order createOrderItems(List<CreateOrderItemSpec> createOrderItemSpecs) {
        for (CreateOrderItemSpec createOrderItemSpec : createOrderItemSpecs) {
            addOrderItem(createOrderItemSpec.productId(), createOrderItemSpec.quantity(), createOrderItemSpec.couponId());
        }
        return this;
    }

    public void successOrder() {
        this.orderStatus = OrderStatus.PAYMENT_COMPLETED;
    }

    public void failOrder() {
        this.orderStatus = OrderStatus.PAYMENT_PENDING;
    }
}
