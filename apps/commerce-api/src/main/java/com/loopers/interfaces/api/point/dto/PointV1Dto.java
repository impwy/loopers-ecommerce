package com.loopers.interfaces.api.point.dto;

import java.math.BigDecimal;

public class PointV1Dto {

    public class Request {
        public record PointChargeRequest(Long memberId, String amount) {
            public static PointChargeRequest of(Long memberId, String amount) {
                return new PointChargeRequest(memberId, amount);
            }
        }
    }

    public class Response {
        public record PointAmountResponse(BigDecimal amount) {
            public static PointAmountResponse of(BigDecimal amount) {
                return new PointAmountResponse(amount);
            }
        }
    }
}
