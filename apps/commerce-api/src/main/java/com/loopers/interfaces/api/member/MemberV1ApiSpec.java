package com.loopers.interfaces.api.member;

import com.loopers.domain.member.MemberRegisterRequest;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.member.dto.MemberV1Dto;

/**
 * Member 관련 API 입니다.
 */
public interface MemberV1ApiSpec {
    ApiResponse<MemberV1Dto.MemberRegisterResponse> register(MemberRegisterRequest registerRequest);
    ApiResponse<MemberV1Dto.MemberInfoResponse> find(Long memberId);
}
