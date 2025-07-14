package com.loopers.interfaces.api.member;

import com.loopers.domain.MemberRegisterRequest;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.member.dto.MemberInfoResponse;
import com.loopers.interfaces.api.member.dto.MemberRegisterResponse;

/**
 * Member 관련 API 입니다.
 */
public interface MemberV1ApiSpec {
    ApiResponse<MemberRegisterResponse> register(MemberRegisterRequest registerRequest);
    ApiResponse<MemberInfoResponse> find(Long memberId);
}
