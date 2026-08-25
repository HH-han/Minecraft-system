package com.minecraft.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    /**
     * CORS 统一在 SecurityConfig 的 SecurityFilterChain 中通过 .cors() 接入，
     * 避免此处的 CorsFilter 作为独立 Servlet 过滤器重复注册导致响应头重复。
     * 保留该配置类便于参考历史参数；核心生效配置请见 SecurityConfig#corsConfigurationSource。
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.setAllowCredentials(true);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        CorsFilter corsFilter = new CorsFilter(source);

        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>(corsFilter);
        // 禁用 Servlet 容器级自动注册，由 Spring Security 链路统一处理 CORS
        registration.setEnabled(false);
        return registration;
    }
}
