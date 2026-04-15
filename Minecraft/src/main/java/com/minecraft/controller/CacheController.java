package com.minecraft.controller;

import com.minecraft.common.Result;
import com.minecraft.service.CacheService;
import com.minecraft.vo.CacheStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 缓存监控控制器
 */
@RestController
@RequestMapping("/api/cache")
public class CacheController {
    
    @Autowired
    private CacheService cacheService;
    
    /**
     * 获取缓存统计信息
     * @return 缓存统计信息
     */
    @GetMapping("/stats")
    public Result getCacheStats() {
        CacheStatsVO stats = cacheService.getCacheStats();
        return Result.success(stats);
    }
    
    /**
     * 获取缓存键列表
     * @param pattern 键模式
     * @return 缓存键列表
     */
    @GetMapping("/keys")
    public Result getCacheKeys(@RequestParam(required = false) String pattern) {
        List<String> keys = cacheService.getCacheKeys(pattern);
        return Result.success(keys);
    }
    
    /**
     * 获取缓存详情
     * @param key 缓存键
     * @return 缓存值
     */
    @GetMapping("/keys/{key}")
    public Result getCacheDetail(@PathVariable String key) {
        Object value = cacheService.getCacheValue(key);
        return Result.success(value);
    }
    
    /**
     * 清除指定缓存
     * @param key 缓存键
     * @return 是否成功
     */
    @DeleteMapping("/keys/{key}")
    public Result clearCache(@PathVariable String key) {
        boolean success = cacheService.clearCache(key);
        return Result.success(success);
    }
    
    /**
     * 清除所有缓存
     * @return 是否成功
     */
    @DeleteMapping("/keys")
    public Result clearAllCache() {
        boolean success = cacheService.clearAllCache();
        return Result.success(success);
    }
}
