package com.banking.repositories.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for caching.
 * Enables caching for frequently accessed data to reduce database load.
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    /**
     * Cache manager bean.
     * Creates a simple in-memory cache manager with predefined cache names.
     * 
     * @return the cache manager
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(java.util.Arrays.asList(
            "creditTypes",
            "activeIndividualCreditTypes",
            "activeCorporateCreditTypes",
            "individualCustomerById",
            "corporateCustomerById",
            "userByEmail",
            "creditApplicationById"
        ));
        return cacheManager;
    }
} 