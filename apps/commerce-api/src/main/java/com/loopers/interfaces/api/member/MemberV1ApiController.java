package com.loopers.interfaces.api.member;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loopers.application.provided.MemberFinder;
import com.loopers.application.provided.MemberRegister;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberRegisterRequest;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.member.dto.MemberV1Dto;
import com.loopers.interfaces.api.member.dto.MemberV1Dto.MemberInfoResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberV1ApiController implements MemberV1ApiSpec {
    private final MemberRegister memberRegister;
    private final MemberFinder memberFinder;

    @PostMapping
    @Override
    public ApiResponse<MemberV1Dto.MemberRegisterResponse> register(@RequestBody @Valid MemberRegisterRequest registerRequest) {
        Member member = memberRegister.register(registerRequest);

        MemberV1Dto.MemberRegisterResponse memberRegisterResponse = MemberV1Dto.MemberRegisterResponse.of(member);
        return ApiResponse.success(memberRegisterResponse);
    }

    @GetMapping("{memberId}")
    @Override
    public ApiResponse<MemberV1Dto.MemberInfoResponse> find(@PathVariable Long memberId, @RequestHeader HttpHeaders httpHeaders) {
        return ApiResponse.success(MemberInfoResponse.of(memberFinder.find(memberId)));
    }
}
