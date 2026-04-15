package com.minecraft.vo;

import lombok.Data;

/**
 * 用户统计信息VO
 */
@Data
public class UserStatsVO {
    /**
     * 在线用户数
     */
    private int onlineCount;
    
    /**
     * 今日登录次数
     */
    private int todayLoginCount;
    
    /**
     * 总用户数
     */
    private int totalCount;
    
    /**
     * 活跃用户数（最近7天）
     */
    private int activeCount;
    
    /**
     * 平均在线时长（分钟）
     */
    private double avgOnlineTime;
    
    /**
     * 登录趋势数据
     */
    private LoginTrendVO loginTrend;
    
    /**
     * 登录趋势内部类
     */
    @Data
    public static class LoginTrendVO {
        /**
         * 时间点
         */
        private String[] timePoints;
        
        /**
         * 登录次数
         */
        private int[] loginCounts;
    }
}
