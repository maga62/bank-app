package com.banking.webapi.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.bucket4j.local.LocalBucketBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitingConfig implements WebMvcConfigurer {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Bean
    public RateLimitInterceptor rateLimitInterceptor() {
        return new RateLimitInterceptor(this::resolveBucket);
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor())
                .addPathPatterns("/api/auth/**")
                .addPathPatterns("/api/auth/login")
                .addPathPatterns("/api/auth/register/**");
    }

    private Bucket resolveBucket(String key) {
        return buckets.computeIfAbsent(key, this::newBucket);
    }

    private Bucket newBucket(String key) {
        LocalBucketBuilder builder = Bucket.builder();
        
        if (key.contains("/api/auth/login")) {
            // Stricter rate limit for login attempts (10 per minute)
            return builder
                    .addLimit(Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1))))
                    .build();
        } else if (key.contains("/api/auth/register")) {
            // Rate limit for registration (5 per minute)
            return builder
                    .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1))))
                    .build();
        } else {
            // Default rate limit (20 per minute)
            return builder
                    .addLimit(Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1))))
                    .build();
        }
    }
} 