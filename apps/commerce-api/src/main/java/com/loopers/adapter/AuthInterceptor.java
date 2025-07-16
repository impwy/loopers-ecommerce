package com.loopers.adapter;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader("X-USER-ID");
        if (header == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "X-USER-ID가 존재하지 않습니다.");
        }
        return true;
    }
}
