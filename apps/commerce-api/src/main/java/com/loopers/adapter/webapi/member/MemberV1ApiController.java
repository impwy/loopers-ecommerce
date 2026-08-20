package com.loopers.adapter.webapi.member;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loopers.application.member.provided.MemberFinder;
import com.loopers.application.member.provided.MemberRegister;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.UserId;
import com.loopers.adapter.webapi.ApiResponse;
import com.loopers.adapter.webapi.member.dto.MemberV1Dto.Request.MemberRegisterRequest;
import com.loopers.adapter.webapi.member.dto.MemberV1Dto;
import com.loopers.adapter.webapi.member.dto.MemberV1Dto.Response.MemberInfoResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberV1ApiController implements MemberV1ApiSpec {
    private final MemberFinder memberFinder;
    private final MemberRegister memberRegister;

    @PostMapping
    @Override
    public ApiResponse<MemberV1Dto.Response.MemberRegisterResponse> register(@RequestBody @Valid MemberRegisterRequest registerRequest) {
        Member member = memberRegister.register(registerRequest);

        MemberV1Dto.Response.MemberRegisterResponse memberRegisterResponse = MemberV1Dto.Response.MemberRegisterResponse.of(member);
        return ApiResponse.success(memberRegisterResponse);
    }

    @GetMapping("/me")
    @Override
    public ApiResponse<MemberV1Dto.Response.MemberInfoResponse> find(UserId userId) {
        return ApiResponse.success(MemberInfoResponse.of(memberFinder.findByUserId(userId)));
    }

    @GetMapping("/points")
    @Override
    public ApiResponse<MemberV1Dto.Response.MemberWithPointResponse> findPoints(UserId userId) {
        return ApiResponse.success(MemberV1Dto.Response.MemberWithPointResponse.of(memberFinder.findWithPoint(userId)));
    }

    @PostMapping("/points/charge")
    @Override
    public ApiResponse<MemberV1Dto.Response.MemberWithPointResponse> chargePoints(UserId userId,
                                                                                  @RequestBody BigDecimal chargePoint) {
        Member member = memberRegister.chargePoint(userId, chargePoint);
        return ApiResponse.success(MemberV1Dto.Response.MemberWithPointResponse.of(member));
    }
}
