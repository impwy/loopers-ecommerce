package com.loopers.interfaces.api.member;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loopers.application.provided.MemberRegister;
import com.loopers.domain.Member;
import com.loopers.domain.MemberRegisterRequest;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.member.dto.MemberRegisterResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberV1ApiController implements MemberV1ApiSpec {
    private final MemberRegister memberRegister;

    @PostMapping
    @Override
    public ApiResponse<MemberRegisterResponse> register(@RequestBody @Valid MemberRegisterRequest registerRequest) {
        Member member = memberRegister.register(registerRequest);

        MemberRegisterResponse memberRegisterResponse = MemberRegisterResponse.of(member);
        return ApiResponse.success(memberRegisterResponse);
    }
}
