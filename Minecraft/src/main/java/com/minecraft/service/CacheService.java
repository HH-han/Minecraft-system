package com.minecraft.service;

import com.minecraft.vo.CacheStatsVO;

import java.util.List;
import java.util.Map;

/**
 * 缓存监控服务接口
 */
public interface CacheService {
    /**
     * 获取缓存统计信息
     * @return 缓存统计信息
     */
    CacheStatsVO getCacheStats();
    
    /**
     * 获取缓存键列表
     * @param pattern 键模式
     * @return 缓存键列表
     */
    List<String> getCacheKeys(String pattern);
    
    /**
     * 获取缓存详情
     * @param key 缓存键
     * @return 缓存值
     */
    Object getCacheValue(String key);
    
    /**
     * 清除指定缓存
     * @param key 缓存键
     * @return 是否成功
     */
    boolean clearCache(String key);
    
    /**
     * 清除所有缓存
     * @return 是否成功
     */
    boolean clearAllCache();
}
