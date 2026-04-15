package com.minecraft.vo;

import lombok.Data;

import java.util.Map;

/**
 * 缓存统计信息VO
 */
@Data
public class CacheStatsVO {
    /**
     * 缓存名称
     */
    private String name;
    
    /**
     * 缓存大小
     */
    private long size;
    
    /**
     * 缓存命中率
     */
    private double hitRate;
    
    /**
     * 缓存未命中率
     */
    private double missRate;
    
    /**
     * 缓存键值对
     */
    private Map<String, Object> entries;
    
    /**
     * 缓存状态
     */
    private String status;
    
    /**
     * 缓存类型
     */
    private String type;
    
    /**
     * 最大容量
     */
    private long maxSize;
    
    /**
     * 内存使用量（字节）
     */
    private long memoryUsage;
}
