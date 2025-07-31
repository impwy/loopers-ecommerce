package com.loopers.interfaces.api.member;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loopers.application.member.MemberFacade;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.member.dto.MemberRegisterRequest;
import com.loopers.interfaces.api.member.dto.MemberV1Dto;
import com.loopers.interfaces.api.member.dto.MemberV1Dto.MemberInfoResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberV1ApiController implements MemberV1ApiSpec {
    private final MemberFacade memberFacade;

    @PostMapping
    @Override
    public ApiResponse<MemberV1Dto.MemberRegisterResponse> register(@RequestBody @Valid MemberRegisterRequest registerRequest) {
        Member member = memberFacade.register(registerRequest);

        MemberV1Dto.MemberRegisterResponse memberRegisterResponse = MemberV1Dto.MemberRegisterResponse.of(member);
        return ApiResponse.success(memberRegisterResponse);
    }

    @GetMapping("/me")
    @Override
    public ApiResponse<MemberV1Dto.MemberInfoResponse> find(MemberId memberId) {
        return ApiResponse.success(MemberInfoResponse.of(memberFacade.find(memberId)));
    }
}
