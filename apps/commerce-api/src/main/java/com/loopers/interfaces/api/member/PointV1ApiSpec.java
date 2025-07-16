package com.loopers.interfaces.api.member;

public interface PointV1ApiSpec {
    String find(String memberId);

    String charge(String memberId, String amount);
}
