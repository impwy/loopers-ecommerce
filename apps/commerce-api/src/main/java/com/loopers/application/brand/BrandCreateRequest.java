package com.loopers.application.brand;

import java.time.LocalDate;

public record BrandCreateRequest(String name, String description, LocalDate since) {
}
