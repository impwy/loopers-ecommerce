package com.loopers.interfaces.api.member.dto;

public class PointV1Dto {

    public class Request {
        public record PointChargeRequest(Long memberId, String amount) {
            public static PointChargeRequest of(Long memberId, String amount) {
                return new PointChargeRequest(memberId, amount);
            }
        }
    }

    public class Response {
        public record PointAmountResponse(String chargedAmount) {
            public static PointAmountResponse of(Long chargedAmount) {
                return new PointAmountResponse(String.valueOf(chargedAmount));
            }
        }
    }
}
