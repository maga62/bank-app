package com.banking.core.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * API isteklerini sınırlamak için kullanılan rate limiter servisi.
 * Belirli bir zaman diliminde, belirli bir IP adresinden gelen istekleri sınırlar.
 */
@Service
@Slf4j
public class RateLimiterService {

    @Value("${rate.limiter.enabled:true}")
    private boolean rateLimiterEnabled;
    
    @Value("${rate.limiter.max.requests:100}")
    private int maxRequests;
    
    @Value("${rate.limiter.window.minutes:1}")
    private int windowMinutes;
    
    // IP adresi bazında istek sayılarını izlemek için
    private final Map<String, RequestCounter> requestCounters = new ConcurrentHashMap<>();
    
    /**
     * Belirli bir IP adresinden gelen isteğin sınırı aşıp aşmadığını kontrol eder.
     * 
     * @param ipAddress İstek yapan IP adresi
     * @return İstek sınırı aşılmışsa true, aşılmamışsa false
     */
    public boolean isRateLimited(String ipAddress) {
        if (!rateLimiterEnabled) {
            return false;
        }
        
        RequestCounter counter = requestCounters.computeIfAbsent(ipAddress, 
                k -> new RequestCounter(LocalDateTime.now()));
        
        // Zaman penceresi dışındaysa sayacı sıfırla
        if (counter.getLastRequestTime().plusMinutes(windowMinutes).isBefore(LocalDateTime.now())) {
            counter.reset(LocalDateTime.now());
        }
        
        // İstek sayısını artır ve kontrol et
        int currentCount = counter.incrementAndGet();
        counter.setLastRequestTime(LocalDateTime.now());
        
        if (currentCount > maxRequests) {
            log.warn("Rate limit exceeded for IP: {}, count: {}", ipAddress, currentCount);
            return true;
        }
        
        return false;
    }
    
    /**
     * İstek sayacı iç sınıfı.
     */
    private static class RequestCounter {
        private final AtomicInteger count;
        private LocalDateTime lastRequestTime;
        
        public RequestCounter(LocalDateTime initialTime) {
            this.count = new AtomicInteger(0);
            this.lastRequestTime = initialTime;
        }
        
        public int incrementAndGet() {
            return count.incrementAndGet();
        }
        
        public void reset(LocalDateTime time) {
            count.set(0);
            this.lastRequestTime = time;
        }
        
        public LocalDateTime getLastRequestTime() {
            return lastRequestTime;
        }
        
        public void setLastRequestTime(LocalDateTime lastRequestTime) {
            this.lastRequestTime = lastRequestTime;
        }
    }
} 