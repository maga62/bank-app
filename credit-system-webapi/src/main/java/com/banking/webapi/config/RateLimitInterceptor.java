package com.banking.webapi.config;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.lang.NonNull;


import java.util.function.Function;

public class RateLimitInterceptor implements HandlerInterceptor {

    private final Function<String, Bucket> bucketResolver;

    public RateLimitInterceptor(Function<String, Bucket> bucketResolver) {
        this.bucketResolver = bucketResolver;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String key = request.getRemoteAddr() + ":" + request.getRequestURI();
        Bucket bucket = bucketResolver.apply(key);
        
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        
        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), 
                    "Rate limit exceeded. Try again in " + waitForRefill + " seconds.");
            return false;
        }
    }
} 