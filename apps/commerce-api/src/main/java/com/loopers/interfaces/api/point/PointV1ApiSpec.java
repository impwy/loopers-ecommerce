package com.loopers.interfaces.api.point;

/**
 * Point 관련 API 입니다.
 */
public interface PointV1ApiSpec {
    String find(String memberId);

    String charge(String memberId, String amount);
}
