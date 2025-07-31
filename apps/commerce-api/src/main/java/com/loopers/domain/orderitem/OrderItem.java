package com.loopers.domain.orderitem;

import java.math.BigDecimal;

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
@Table(name = "order_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {
    private Long orderId;

    private Long productId;

    private Long quantity;

    private BigDecimal price;

    private OrderItem(Long orderId, Long productId, Long quantity, BigDecimal price) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public static OrderItem create(Long orderId, Long productId, Long quantity, BigDecimal price) {
        if (orderId == null || orderId <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "유저는 필수입니다.");
        }
        if (productId == null || productId <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품은 필수입니다.");
        }
        if (quantity == null || quantity <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 수량입니다. : " + quantity);
        }
        if (price == null || price.doubleValue() <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 총 가격입니다. : " + price);
        }
        return new OrderItem(orderId, productId, quantity, price);
    }

    public static OrderItem create(CreateOrderItemSpec createSpec) {
        return create(createSpec.orderId(), createSpec.productId(), createSpec.quantity(), createSpec.totalPrice());
    }
}
