package com.loopers.domain.order;

import java.time.LocalDate;
import java.util.Random;

import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class OrderNo {
    private static final Random RANDOM = new Random();
    private String value;

    public OrderNo(String value) {this.value = value;}

    public static OrderNo newOrderNo() {
        return new OrderNo(createOrderNo());
    }

    private static String createOrderNo() {
        LocalDate now = LocalDate.now();

        String datePart = String.format("%04d%02d%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());

        int randomNumber = 100000 + RANDOM.nextInt(900000); // 6자리 랜덤 숫자

        return datePart + randomNumber;
    }

    public String value() {return value;}
}
