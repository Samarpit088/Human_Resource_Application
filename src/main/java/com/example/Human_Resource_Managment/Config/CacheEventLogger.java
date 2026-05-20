package com.example.Human_Resource_Managment.Config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Aspect to log cache operations for debugging
 */
@Aspect
@Component
@Slf4j
public class CacheEventLogger {

    @Around("@annotation(org.springframework.cache.annotation.Cacheable)")
    public Object logCacheable(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Cacheable cacheable = method.getAnnotation(Cacheable.class);
        
        String cacheName = cacheable.value().length > 0 ? cacheable.value()[0] : "unknown";
        Object[] args = joinPoint.getArgs();
        
        log.debug("[@Cacheable] Attempting to read from cache '{}' for method: {} with args: {}", 
                cacheName, method.getName(), Arrays.toString(args));
        
        Object result = joinPoint.proceed();
        
        log.debug("[@Cacheable] Cache operation completed for '{}'", cacheName);
        return result;
    }

    @Around("@annotation(org.springframework.cache.annotation.CacheEvict)")
    public Object logCacheEvict(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);
        
        String cacheName = cacheEvict.value().length > 0 ? cacheEvict.value()[0] : "unknown";
        Object[] args = joinPoint.getArgs();
        
        log.info("=== [@CacheEvict] EVICTING cache '{}' for method: {} with args: {} ===", 
                cacheName, method.getName(), Arrays.toString(args));
        
        Object result = joinPoint.proceed();
        
        log.info("=== [@CacheEvict] Cache '{}' EVICTED successfully ===", cacheName);
        return result;
    }

    @Around("@annotation(org.springframework.cache.annotation.Caching)")
    public Object logCaching(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        
        log.info("=== [@Caching] Multiple cache operations for method: {} with args: {} ===", 
                method.getName(), Arrays.toString(args));
        
        Object result = joinPoint.proceed();
        
        log.info("=== [@Caching] All cache operations completed for method: {} ===", method.getName());
        return result;
    }
}
