package com.loopers.interfaces.api.point;

public interface PointV1ApiSpec {
    String find(String memberId);

    String charge(String memberId, String amount);
}
