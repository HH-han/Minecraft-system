package com.minecraft.mapper;

import com.minecraft.vo.OnlineUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 在线用户Mapper接口
 */
@Mapper
public interface OnlineUserMapper {
    /**
     * 获取在线用户列表
     * @param offset 偏移量
     * @param size 每页大小
     * @return 在线用户列表
     */
    List<OnlineUserVO> getOnlineUsers(@Param("offset") int offset, @Param("size") int size);
    
    /**
     * 获取在线用户总数
     * @return 在线用户总数
     */
    int getOnlineUserCount();
    
    /**
     * 根据用户ID获取在线用户
     * @param userId 用户ID
     * @return 在线用户
     */
    OnlineUserVO getOnlineUserById(@Param("userId") Long userId);
    
    /**
     * 删除在线用户
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteOnlineUser(@Param("userId") Long userId);
    
    /**
     * 添加在线用户
     * @param onlineUser 在线用户
     * @return 影响行数
     */
    int addOnlineUser(OnlineUserVO onlineUser);
    
    /**
     * 获取今日登录次数
     * @return 今日登录次数
     */
    int getTodayLoginCount();
    
    /**
     * 获取总用户数
     * @return 总用户数
     */
    int getTotalUserCount();
    
    /**
     * 获取活跃用户数
     * @param days 天数
     * @return 活跃用户数
     */
    int getActiveUserCount(@Param("days") int days);
    
    /**
     * 获取登录趋势数据
     * @param hours 小时数
     * @return 登录趋势数据
     */
    List<Map<String, Object>> getLoginTrend(@Param("hours") int hours);
    
    /**
     * 获取用户活动日志
     * @param userId 用户ID
     * @param days 天数
     * @return 活动日志
     */
    List<Map<String, Object>> getUserActivity(@Param("userId") Long userId, @Param("days") int days);
}
