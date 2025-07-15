package com.loopers.interfaces.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loopers.interfaces.api.member.dto.PointV1Dto;
import com.loopers.interfaces.api.member.dto.PointV1Dto.Request.PointChargeRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/points")
public class PointV1ApiController {

    @PostMapping("/charge/{memberId}")
    public ApiResponse<PointV1Dto.Response.PointAmountResponse> charge(@PathVariable("memberId") Long memberId,
                                                                       @RequestBody PointChargeRequest request) {
        //TODO: 실제 Member Point 충전 구현
        return ApiResponse.success(PointV1Dto.Response.PointAmountResponse.of(Long.valueOf(request.amount())));
    }
}
