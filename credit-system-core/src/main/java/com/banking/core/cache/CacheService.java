package com.banking.core.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Optional;

/**
 * Önbellekleme işlemleri için yardımcı servis.
 * Önbellek yönetimi ve manuel önbellekleme işlemleri için kullanılır.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private final CacheManager cacheManager;

    /**
     * Belirtilen önbellekteki bir anahtarı temizler
     * 
     * @param cacheName Önbellek adı
     * @param key Anahtar
     */
    public void evictSingleCacheValue(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
            log.debug("Cache entry evicted. Cache: {}, Key: {}", cacheName, key);
        } else {
            log.warn("Cache not found: {}", cacheName);
        }
    }

    /**
     * Belirtilen önbelleği tamamen temizler
     * 
     * @param cacheName Önbellek adı
     */
    public void evictAllCacheValues(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.debug("Cache cleared: {}", cacheName);
        } else {
            log.warn("Cache not found: {}", cacheName);
        }
    }

    /**
     * Tüm önbellekleri temizler
     */
    public void evictAllCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
        log.debug("All caches cleared");
    }

    /**
     * Belirtilen önbellekte bir değer saklar
     * 
     * @param cacheName Önbellek adı
     * @param key Anahtar
     * @param value Değer
     */
    public void putCacheValue(String cacheName, String key, Object value) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.put(key, value);
            log.debug("Value cached. Cache: {}, Key: {}", cacheName, key);
        } else {
            log.warn("Cache not found: {}", cacheName);
        }
    }

    /**
     * Belirtilen önbellekten bir değer alır
     * 
     * @param cacheName Önbellek adı
     * @param key Anahtar
     * @param clazz Değer sınıfı
     * @return Önbellekteki değer
     */
    public <T> T getCacheValue(String cacheName, String key, Class<T> clazz) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            Cache.ValueWrapper valueWrapper = cache.get(key);
            if (valueWrapper != null) {
                Object value = valueWrapper.get();
                if (clazz.isInstance(value)) {
                    log.debug("Cache hit. Cache: {}, Key: {}", cacheName, key);
                    return clazz.cast(value);
                }
            }
            log.debug("Cache miss. Cache: {}, Key: {}", cacheName, key);
        } else {
            log.warn("Cache not found: {}", cacheName);
        }
        return null;
    }

    public <T> boolean put(String key, T value, Duration duration) {
        // Implementation needed
        return false;
    }

    public <T> Optional<T> get(String key, Class<T> type) {
        // Implementation needed
        return Optional.empty();
    }

    public boolean remove(String key) {
        // Implementation needed
        return false;
    }
} 