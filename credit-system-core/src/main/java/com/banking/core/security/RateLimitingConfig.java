package com.banking.core.security;

import com.banking.core.logging.LogAnalyticsService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * API hız sınırlama yapılandırması.
 * Belirli IP adresleri veya API anahtarları için istek sınırlaması uygular.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class RateLimitingConfig implements WebMvcConfigurer {

    private final LogAnalyticsService logAnalyticsService;

    @Value("${rate.limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${rate.limit.requests.per.second:10}")
    private int requestsPerSecond;

    @Value("${rate.limit.burst:20}")
    private int burst;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        if (rateLimitEnabled) {
            registry.addInterceptor(rateLimitInterceptor())
                    .addPathPatterns("/api/**")
                    .excludePathPatterns("/api/public/**", "/api/auth/**");
        }
    }

    @Bean
    public HandlerInterceptor rateLimitInterceptor() {
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
                String identifier = getClientIdentifier(request);
                Bucket bucket = buckets.computeIfAbsent(identifier, this::createNewBucket);

                if (bucket.tryConsume(1)) {
                    return true;
                } else {
                    try {
                        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                        response.getWriter().write("Rate limit exceeded. Please try again later.");
                        
                        // Hız sınırı aşımını logla
                        logAnalyticsService.logSystemEvent(
                                "RATE_LIMIT_EXCEEDED",
                                "RateLimitingInterceptor",
                                Map.of(
                                        "clientIp", request.getRemoteAddr(),
                                        "path", request.getRequestURI(),
                                        "method", request.getMethod(),
                                        "userAgent", request.getHeader("User-Agent")
                                )
                        );
                        
                        log.warn("Rate limit exceeded for client: {}, URI: {}", identifier, request.getRequestURI());
                    } catch (Exception e) {
                        log.error("Error while handling rate limit response", e);
                    }
                    return false;
                }
            }

            private Bucket createNewBucket(String key) {
                Bandwidth limit = Bandwidth.classic(burst, Refill.greedy(requestsPerSecond, Duration.ofSeconds(1)));
                return Bucket4j.builder().addLimit(limit).build();
            }

            private String getClientIdentifier(HttpServletRequest request) {
                // Öncelikle API anahtarını kontrol et
                String apiKey = request.getHeader("X-API-KEY");
                if (apiKey != null && !apiKey.isEmpty()) {
                    return "api:" + apiKey;
                }
                
                // API anahtarı yoksa IP adresini kullan
                String clientIp = request.getHeader("X-Forwarded-For");
                if (clientIp == null || clientIp.isEmpty()) {
                    clientIp = request.getRemoteAddr();
                }
                return "ip:" + clientIp;
            }
        };
    }
} 