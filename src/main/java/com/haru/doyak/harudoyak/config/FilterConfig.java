//package com.haru.doyak.harudoyak.config;
//
//import com.haru.doyak.harudoyak.security.JwtAuthorizationFilter;
//import com.haru.doyak.harudoyak.security.JwtProvider;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class FilterConfig {
//    private final JwtProvider jwtProvider;
//
//    public FilterConfig(JwtProvider jwtProvider) {
//        this.jwtProvider = jwtProvider;
//    }
//
//    @Bean
//    public FilterRegistrationBean<JwtAuthorizationFilter> jwtFilter() {
//        FilterRegistrationBean<JwtAuthorizationFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(new JwtAuthorizationFilter(jwtProvider));
//        registrationBean.addUrlPatterns("/api/auth/validate/**");
//        registrationBean.setOrder(1);
//        return registrationBean;
//    }
//}
