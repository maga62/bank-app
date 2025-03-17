package com.banking.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * Asenkron işlemler için yapılandırma sınıfı.
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    @Value("${async.core.pool.size:5}")
    private int corePoolSize;

    @Value("${async.max.pool.size:10}")
    private int maxPoolSize;

    @Value("${async.queue.capacity:25}")
    private int queueCapacity;

    @Value("${async.thread.name.prefix:banking-async-}")
    private String threadNamePrefix;

    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        
        log.info("Async task executor initialized with core pool size: {}, max pool size: {}, queue capacity: {}", 
                corePoolSize, maxPoolSize, queueCapacity);
        
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }

    /**
     * Asenkron işlemlerde yakalanan hataları işleyen sınıf.
     */
    private static class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
        @Override
        public void handleUncaughtException(@NonNull Throwable ex, @NonNull Method method, @NonNull Object... params) {
            log.error("Async method '{}' threw exception: {}", method.getName(), ex.getMessage(), ex);
            
            StringBuilder paramInfo = new StringBuilder();
            for (int i = 0; i < params.length; i++) {
                paramInfo.append("Parameter value ").append(i + 1).append(": ");
                if (params[i] == null) {
                    paramInfo.append("null");
                } else {
                    paramInfo.append(params[i].toString());
                }
                if (i < params.length - 1) {
                    paramInfo.append(", ");
                }
            }
            
            log.error("Method parameters: {}", paramInfo);
        }
    }
} 