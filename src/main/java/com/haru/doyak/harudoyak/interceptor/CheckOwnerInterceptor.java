package com.haru.doyak.harudoyak.interceptor;

import com.haru.doyak.harudoyak.annotation.Authenticated;
import com.haru.doyak.harudoyak.security.AuthenticatedUser;
import com.haru.doyak.harudoyak.security.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Parameter;

// jwt memberId와 api pathvalue의 memberId가 동일한지
@Component
@RequiredArgsConstructor
public class CheckOwnerInterceptor implements HandlerInterceptor {

    @Value("${jwt.atk.typ}")
    private String atkType;
    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(handler instanceof HandlerMethod) {
            // 핸들러가 메서드일 경우에만 진행

            // 파라미터에 Authenticated 어노테이션이 있는 지 확인
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Parameter[] parameters = handlerMethod.getMethod().getParameters();
            for(Parameter parameter : parameters){

                if(parameter.isAnnotationPresent(Authenticated.class)){
                    // request 에서 토큰 추출하기
                    String token = jwtProvider.parseBearerToken(request);
                    // jwt를 검증히고 claim을 가져와 인증된 사용자 객체를 만들고 넣기
                    AuthenticatedUser authenticatedUser = getLoginMember(token);
                    request.setAttribute("authenticatedUser", authenticatedUser);
                }
            }
        }
        return true;
    }

    private AuthenticatedUser getLoginMember(String token){
        return new AuthenticatedUser(jwtProvider.validateAndExtractClaims(token, atkType));
    }
}
