package com.loopers.domain.order;

import jakarta.persistence.Embeddable;

@Embeddable
public record Address(String zipCode, String sido, String gugun, String dong, String etc) {
}
