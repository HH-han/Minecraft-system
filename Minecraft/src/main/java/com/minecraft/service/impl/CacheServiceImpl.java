package com.minecraft.service.impl;

import com.minecraft.service.CacheService;
import com.minecraft.vo.CacheStatsVO;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存监控服务实现类
 */
@Service
public class CacheServiceImpl implements CacheService {
    
    // 模拟缓存存储
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    
    // 统计信息
    private long hits = 0;
    private long misses = 0;
    
    // 缓存条目内部类
    private static class CacheEntry {
        private final Object value;
        private final long timestamp;
        private final long accessCount;
        
        public CacheEntry(Object value) {
            this.value = value;
            this.timestamp = System.currentTimeMillis();
            this.accessCount = 0;
        }
    }
    
    public CacheServiceImpl() {
        // 初始化一些模拟缓存数据
        initializeCache();
    }
    
    private void initializeCache() {
        // 模拟用户信息缓存
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", 1);
        userInfo.put("username", "admin");
        userInfo.put("role", "admin");
        cache.put("user:1", new CacheEntry(userInfo));
        
        // 模拟配置缓存
        Map<String, Object> config = new HashMap<>();
        config.put("appName", "Minecraft System");
        config.put("version", "1.0.0");
        config.put("env", "production");
        cache.put("config:app", new CacheEntry(config));
        
        // 模拟统计数据缓存
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", 1000);
        stats.put("totalOrders", 5000);
        stats.put("totalRevenue", 100000);
        cache.put("stats:system", new CacheEntry(stats));
        
        // 模拟热点数据缓存
        cache.put("hot:products", new CacheEntry(Arrays.asList(1, 2, 3, 4, 5)));
        cache.put("hot:categories", new CacheEntry(Arrays.asList("electronics", "clothing", "food")));
    }
    
    @Override
    public CacheStatsVO getCacheStats() {
        CacheStatsVO stats = new CacheStatsVO();
        stats.setName("Redis Cache");
        stats.setSize(cache.size());
        
        // 计算命中率
        long total = hits + misses;
        stats.setHitRate(total > 0 ? (double) hits / total * 100 : 0);
        stats.setMissRate(total > 0 ? (double) misses / total * 100 : 0);
        
        // 构建缓存条目
        Map<String, Object> entries = new HashMap<>();
        for (Map.Entry<String, CacheEntry> entry : cache.entrySet()) {
            entries.put(entry.getKey(), entry.getValue().value);
        }
        stats.setEntries(entries);
        
        stats.setStatus("active");
        stats.setType("in-memory");
        stats.setMaxSize(1000);
        stats.setMemoryUsage(calculateMemoryUsage());
        
        return stats;
    }
    
    @Override
    public List<String> getCacheKeys(String pattern) {
        List<String> keys = new ArrayList<>();
        for (String key : cache.keySet()) {
            if (pattern == null || key.matches(pattern.replace("*", ".*"))) {
                keys.add(key);
            }
        }
        return keys;
    }
    
    @Override
    public Object getCacheValue(String key) {
        CacheEntry entry = cache.get(key);
        if (entry != null) {
            hits++;
            return entry.value;
        } else {
            misses++;
            return null;
        }
    }
    
    @Override
    public boolean clearCache(String key) {
        return cache.remove(key) != null;
    }
    
    @Override
    public boolean clearAllCache() {
        cache.clear();
        return true;
    }
    
    private long calculateMemoryUsage() {
        // 模拟内存使用计算
        return cache.size() * 1024; // 每个条目约1KB
    }
}
