package com.loopers.domain.member;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

import static com.loopers.domain.member.Gender.MALE;
import static org.instancio.Select.field;

import com.loopers.application.member.MemberRegisterRequest;

import org.instancio.Instancio;

public class MemberFixture {
    private static final AtomicLong KEY_SEQUENCE = new AtomicLong(1);
    private static final String DEFAULT_PASSWORD = "secret";
    private static final String DEFAULT_EMAIL_DOMAIN = "@loopers.app";
    private static final String DEFAULT_BIRTHDAY = "2025-07-13";

    public static Member createMember() {
        String key = nextKey();
        return Instancio.of(Member.class)
                        .ignore(field(Member::getId))
                        .set(field(Member::getUserId), new UserId(key))
                        .generate(field(Member::getPasswordHash), gen -> gen.string().minLength(1).maxLength(20))
                        .generate(field(Member::getGender), gen -> gen.enumOf(Gender.class))
                        .set(field(Member::getEmail), new Email(key + DEFAULT_EMAIL_DOMAIN))
                        .set(field(Member::getBirthday), LocalDate.parse(DEFAULT_BIRTHDAY))
                        .set(field(Member::getPoint), Point.create())
                        .create();
    }

    public static Member createMember(String key) {
        return Instancio.of(Member.class)
                        .ignore(field(Member::getId))
                        .set(field(Member::getUserId), new UserId(key))
                        .set(field(Member::getPasswordHash), DEFAULT_PASSWORD)
                        .set(field(Member::getGender), MALE)
                        .set(field(Member::getEmail), new Email(key + DEFAULT_EMAIL_DOMAIN))
                        .set(field(Member::getBirthday), LocalDate.parse(DEFAULT_BIRTHDAY))
                        .set(field(Member::getPoint), Point.create())
                        .create();
    }

    public static MemberRegisterRequest createMemberRegisterRequest() {
        String key = nextKey();
        return Instancio.of(MemberRegisterRequest.class)
                        .set(field(MemberRegisterRequest::memberId), key)
                        .generate(field(MemberRegisterRequest::password), gen -> gen.string().minLength(1).maxLength(20))
                        .generate(field(MemberRegisterRequest::gender), gen -> gen.enumOf(Gender.class))
                        .set(field(MemberRegisterRequest::email), key + DEFAULT_EMAIL_DOMAIN)
                        .set(field(MemberRegisterRequest::birthday), DEFAULT_BIRTHDAY)
                        .create();
    }

    public static MemberRegisterRequest createMemberRegisterRequest(String key) {
        return createMemberRegisterRequest(key, DEFAULT_PASSWORD, MALE, key + DEFAULT_EMAIL_DOMAIN, DEFAULT_BIRTHDAY);
    }

    public static MemberRegisterRequest createMemberRegisterRequest(String memberId, String password,
                                                                     Gender gender, String email, String birthday) {
        return Instancio.of(MemberRegisterRequest.class)
                        .set(field(MemberRegisterRequest::memberId), memberId)
                        .set(field(MemberRegisterRequest::password), password)
                        .set(field(MemberRegisterRequest::gender), gender)
                        .set(field(MemberRegisterRequest::email), email)
                        .set(field(MemberRegisterRequest::birthday), birthday)
                        .create();
    }

    private static String nextKey() {
        return "m" + String.format("%09d", KEY_SEQUENCE.getAndIncrement());
    }
}
