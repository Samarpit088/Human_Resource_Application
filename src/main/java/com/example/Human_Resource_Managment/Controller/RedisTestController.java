package com.example.Human_Resource_Managment.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/redis")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RedisTestController {

    private final RedisConnectionFactory redisConnectionFactory;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheManager cacheManager;

    /**
     * Test Redis connectivity
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testRedis() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Test connection
            String pingResult = redisConnectionFactory.getConnection().ping();
            response.put("connected", true);
            response.put("ping", pingResult);
            
            // Test write/read
            String testKey = "test:key";
            String testValue = "test-value-" + System.currentTimeMillis();
            redisTemplate.opsForValue().set(testKey, testValue);
            Object readValue = redisTemplate.opsForValue().get(testKey);
            
            response.put("writeTest", testValue);
            response.put("readTest", readValue);
            response.put("writeReadMatch", testValue.equals(readValue));
            
            // Clean up
            redisTemplate.delete(testKey);
            
            response.put("status", "SUCCESS");
            log.info("Redis test successful");
            
        } catch (Exception e) {
            response.put("connected", false);
            response.put("error", e.getMessage());
            response.put("status", "FAILED");
            log.error("Redis test failed: {}", e.getMessage(), e);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get cache statistics
     */
    @GetMapping("/cache-stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            response.put("cacheManager", cacheManager.getClass().getSimpleName());
            response.put("cacheNames", cacheManager.getCacheNames());
            
            // Check specific caches
            Map<String, Object> cacheDetails = new HashMap<>();
            for (String cacheName : cacheManager.getCacheNames()) {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    cacheDetails.put(cacheName, cache.getNativeCache().getClass().getSimpleName());
                }
            }
            response.put("cacheDetails", cacheDetails);
            response.put("status", "SUCCESS");
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", "FAILED");
            log.error("Failed to get cache stats: {}", e.getMessage(), e);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Clear specific cache
     */
    @DeleteMapping("/cache/{cacheName}")
    public ResponseEntity<Map<String, Object>> clearCache(@PathVariable String cacheName) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                response.put("status", "SUCCESS");
                response.put("message", "Cache '" + cacheName + "' cleared successfully");
                log.info("Cleared cache: {}", cacheName);
            } else {
                response.put("status", "NOT_FOUND");
                response.put("message", "Cache '" + cacheName + "' not found");
            }
        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("error", e.getMessage());
            log.error("Failed to clear cache {}: {}", cacheName, e.getMessage(), e);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Clear all caches
     */
    @DeleteMapping("/cache/all")
    public ResponseEntity<Map<String, Object>> clearAllCaches() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            int clearedCount = 0;
            for (String cacheName : cacheManager.getCacheNames()) {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    cache.clear();
                    clearedCount++;
                }
            }
            response.put("status", "SUCCESS");
            response.put("message", "Cleared " + clearedCount + " caches");
            response.put("clearedCaches", cacheManager.getCacheNames());
            log.info("Cleared all caches: {}", clearedCount);
            
        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("error", e.getMessage());
            log.error("Failed to clear all caches: {}", e.getMessage(), e);
        }
        
        return ResponseEntity.ok(response);
    }
}
