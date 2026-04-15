package com.minecraft.service;

import com.minecraft.vo.OnlineUserVO;
import com.minecraft.vo.UserStatsVO;

import java.util.List;

/**
 * 在线用户监控服务接口
 */
public interface OnlineUserService {
    /**
     * 获取在线用户列表
     * @param page 页码
     * @param size 每页大小
     * @return 在线用户列表
     */
    List<OnlineUserVO> getOnlineUsers(int page, int size);
    
    /**
     * 获取用户统计信息
     * @return 用户统计信息
     */
    UserStatsVO getUserStats();
    
    /**
     * 强制用户下线
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean forceLogout(Long userId);
    
    /**
     * 获取用户活动日志
     * @param userId 用户ID
     * @param days 天数
     * @return 活动日志
     */
    List<String> getUserActivity(Long userId, int days);
}
