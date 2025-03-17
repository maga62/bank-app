package com.banking.core.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * API isteklerini sınırlamak için kullanılan interceptor.
 * Her HTTP isteği için RateLimiterService'i kullanarak rate limiting uygular.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimiterInterceptor implements HandlerInterceptor {

    private final RateLimiterService rateLimiterService;
    
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String ipAddress = getClientIpAddress(request);
        
        if (rateLimiterService.isRateLimited(ipAddress)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded. Please try again later.");
            return false;
        }
        
        return true;
    }
    
    /**
     * İstemcinin IP adresini alır.
     * X-Forwarded-For header'ı varsa onu kullanır, yoksa request.getRemoteAddr() kullanır.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For header'ı virgülle ayrılmış IP adresleri içerebilir
            // İlk IP adresi gerçek istemci IP'sidir
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
} 