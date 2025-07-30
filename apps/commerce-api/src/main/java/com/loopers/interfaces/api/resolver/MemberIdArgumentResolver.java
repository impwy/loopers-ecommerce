package com.loopers.interfaces.api.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.loopers.domain.member.MemberId;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

@Component
public class MemberIdArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String X_USER_ID = "X-USER-ID";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(MemberId.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        String header = webRequest.getHeader(X_USER_ID);
        if (header == null || header.isBlank()) {
            throw new CoreException(ErrorType.NOT_FOUND, parameter.getParameterName());
        }

        MemberId memberId = new MemberId(header);
        return memberId;
    }
}
