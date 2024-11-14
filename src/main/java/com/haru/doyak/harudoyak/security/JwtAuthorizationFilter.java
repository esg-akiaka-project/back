//package com.haru.doyak.harudoyak.security;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpHeaders;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestAttributes;
//import org.springframework.web.context.request.RequestContextHolder;
//
//import java.io.IOException;
//import java.util.Map;
//import java.util.Optional;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthorizationFilter implements Filter {
//
//    private final JwtProvider jwtProvider;
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//
//        String token = parseBearerToken(request);
//
//        if(jwtProvider.validateAndExtractClaims(token, "access")!=null) {
//            Map<String, Object> claims = jwtProvider.extractClaimsFromJwt(token);
//            // 인증 객체 넣기
//            RequestContextHolder.getRequestAttributes()
//                    .setAttribute("authenticated", claims, RequestAttributes.SCOPE_REQUEST);
//
//        }else {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        }
//
//        filterChain.doFilter(servletRequest, servletResponse);
//    }
//
//    private String parseBearerToken(HttpServletRequest request) {
//        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
//                .filter(token -> token.length() > 7 && token.startsWith("Bearer "))
//                .map(token -> token.substring(7))
//                .orElse(null);
//    }
//
//}
