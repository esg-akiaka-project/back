/*
package com.haru.doyak.harudoyak.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
    public class WebConfig */
/*implements WebMvcConfigurer*//*
 {

        @Value("${serverbBaseUrl}")
        private String serverUrl;

        @Bean
        public FilterRegistrationBean<CorsFilter> corsFilter() {
            CorsConfiguration config = new CorsConfiguration();
//            config.setAllowCredentials(true); // 쿠키 및 인증 정보 허용
            config.addAllowedOrigin("*");// 허용할 출처
            */
/*config.addAllowedHeader("*");*//*
 // 모든 헤더 허용  // 모든 HTTP 메서드 허용 (GET, POST, PUT, PATCH, DELETE, OPTIONS 등)
            config.addAllowedMethod("*");
//            config.addExposedHeader("*"); //
            config.setMaxAge(3600L); // preflight 요청의 캐시 시간 설정 (초 단위)

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", config); // 모든 경로에 CORS 설정 적용

            FilterRegistrationBean<CorsFilter> corsFilterBean = new FilterRegistrationBean<>(new CorsFilter(source));
            corsFilterBean.setOrder(0); // 가장 먼저 실행되도록 설정

            return corsFilterBean;
        }

    // 리액트 CORS설정
    */
/*@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://*:3000")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);

    }*//*


    // CORS 필터 등록
*/
/*    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {

        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("http://localhost:3000");
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("OPTIONS");
        corsConfig.addAllowedHeader("*");  // 모든 헤더 허용
        corsConfig.setAllowCredentials(true);  // 자격 증명 허용
        corsConfig.setMaxAge(3600L);  // Preflight 요청에 대한 캐시 시간 설정 (1시간)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);  // 모든 엔드포인트에 CORS 설정

        FilterRegistrationBean<CorsFilter> corsFilterBean = new FilterRegistrationBean<>(new CorsFilter(source));
        corsFilterBean.addUrlPatterns("/api/*"); // CORS 필터를 적용할 URL 패턴
        corsFilterBean.setOrder(0); // 가장 먼저 실행되도록 설정

        return corsFilterBean;
    }*//*


}*/
