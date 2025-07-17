package com.loopers.interfaces.api.member;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loopers.application.MemberFacade;
import com.loopers.domain.member.Member;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.member.dto.MemberV1Dto.Request.MemberRegisterRequest;
import com.loopers.interfaces.api.member.dto.MemberV1Dto.Response;
import com.loopers.interfaces.api.member.dto.MemberV1Dto.Response.MemberInfoResponse;
import com.loopers.interfaces.api.member.dto.MemberV1Dto.Response.MemberRegisterResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberV1ApiController implements MemberV1ApiSpec {
    private final MemberFacade memberFacade;

    @PostMapping
    @Override
    public ApiResponse<MemberRegisterResponse> register(@RequestBody @Valid MemberRegisterRequest registerRequest) {
        Member member = memberFacade.register(registerRequest);

        MemberRegisterResponse memberRegisterResponse = MemberRegisterResponse.of(member);
        return ApiResponse.success(memberRegisterResponse);
    }

    @GetMapping("/me/{memberId}")
    @Override
    public ApiResponse<MemberInfoResponse> find(@PathVariable Long memberId, @RequestHeader HttpHeaders httpHeaders) {
        return ApiResponse.success(Response.MemberInfoResponse.of(memberFacade.find(memberId)));
    }
}
