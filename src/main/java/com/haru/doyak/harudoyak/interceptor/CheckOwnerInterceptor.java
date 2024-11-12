package com.haru.doyak.harudoyak.interceptor;

import com.haru.doyak.harudoyak.annotation.CheckOwner;
import com.haru.doyak.harudoyak.util.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.Optional;

// jwt memberId와 api pathvalue의 memberId가 동일한지
@Component
@RequiredArgsConstructor
public class CheckOwnerInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod) {
            // 핸들러가 메서드일 경우에만 진행
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            // CheckOwner 어노테이션이 있는 지 확인
            if(handlerMethod.getMethod().isAnnotationPresent(CheckOwner.class)){
                // jwt의 memberId와 pathValue의 memberId가 같은지
                Map<String, Object> claims = getLoginMember(request);
                Object memberIdClaim = claims.get("memberId");
                Long jwtMemberId;
                if (memberIdClaim instanceof Integer) {
                    jwtMemberId = ((Integer) memberIdClaim).longValue();
                } else if (memberIdClaim instanceof Long) {
                    jwtMemberId = (Long) memberIdClaim;
                } else {
                    throw new IllegalArgumentException("Invalid memberId type in claims");
                }
                System.out.println("jwt memberId : "+claims.get("memberId"));
                System.out.println("path value : "+ extractMemberIdFromRequest(request));
                if(jwtMemberId==extractMemberIdFromRequest(request)){
                    return true;
                }else {
                    // 401 Unauthorized 설정
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Unauthorized access: Invalid memberId.");
                    response.getWriter().flush();
                    return false;
                }
            }
            return true;
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    Map<String, Object> getLoginMember(HttpServletRequest request){
        String token = jwtProvider.parseBearerToken(request);
        return jwtProvider.extractClaimsFromJwt(token);
    }

    Long extractMemberIdFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(
                request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)
        ).map(Map.class::cast)
                .map(map-> map.get("memberId"))
                .map(Object::toString)
                .map(Long::parseLong)
                .orElseThrow();
    }

}
