package com.banking.webapi.config;

import com.banking.core.security.RateLimiterInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web yapılandırması için kullanılan sınıf.
 * Interceptor'ları ve diğer web yapılandırmalarını içerir.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RateLimiterInterceptor rateLimiterInterceptor;
    
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(rateLimiterInterceptor)
                .addPathPatterns("/api/**") // Tüm API isteklerine uygula
                .excludePathPatterns("/api/auth/**"); // Auth isteklerini hariç tut
    }
} 