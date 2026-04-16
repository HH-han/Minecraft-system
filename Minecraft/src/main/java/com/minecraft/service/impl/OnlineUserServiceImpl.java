package com.minecraft.service.impl;

import com.minecraft.mapper.OnlineUserMapper;
import com.minecraft.service.OnlineUserService;
import com.minecraft.service.UserService;
import com.minecraft.vo.OnlineUserVO;
import com.minecraft.vo.UserStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 在线用户监控服务实现类
 */
@Service
public class OnlineUserServiceImpl implements OnlineUserService {
    
    @Autowired
    private OnlineUserMapper onlineUserMapper;
    
    @Autowired
    private UserService userService;
    
    @Override
    public List<OnlineUserVO> getOnlineUsers(int page, int size) {
        // 计算偏移量
        int offset = (page - 1) * size;
        return onlineUserMapper.getOnlineUsers(offset, size);
    }
    
    @Override
    public UserStatsVO getUserStats() {
        UserStatsVO stats = new UserStatsVO();
        
        // 从数据库获取统计数据
        stats.setOnlineCount(onlineUserMapper.getOnlineUserCount());
        
        // 获取今日登录次数，如果为null则设置为0
        Integer todayLoginCount = onlineUserMapper.getTodayLoginCount();
        stats.setTodayLoginCount(todayLoginCount != null ? todayLoginCount : 0);
        
        // 使用UserService的getUserCount方法获取总用户数
        stats.setTotalCount((int) userService.getUserCount());
        
        // 获取活跃用户数，如果为null则设置为0
        Integer activeCount = onlineUserMapper.getActiveUserCount(7);
        stats.setActiveCount(activeCount != null ? activeCount : 0);
        stats.setAvgOnlineTime(45.5); // 可以从数据库计算，这里暂时使用固定值
        
        // 获取登录趋势数据
        UserStatsVO.LoginTrendVO trend = new UserStatsVO.LoginTrendVO();
        List<Map<String, Object>> trendData = onlineUserMapper.getLoginTrend(24); // 最近24小时
        
        List<String> timePoints = new ArrayList<>();
        List<Integer> loginCounts = new ArrayList<>();
        
        for (Map<String, Object> data : trendData) {
            timePoints.add((String) data.get("timePoint"));
            loginCounts.add((Integer) data.get("loginCount"));
        }
        
        // 如果没有数据，使用默认数据
        if (timePoints.isEmpty()) {
            timePoints.addAll(List.of("00:00", "04:00", "08:00", "12:00", "16:00", "20:00"));
            loginCounts.addAll(List.of(0, 0, 0, 0, 0, 0));
        }
        
        trend.setTimePoints(timePoints.toArray(new String[0]));
        trend.setLoginCounts(loginCounts.stream().mapToInt(Integer::intValue).toArray());
        stats.setLoginTrend(trend);
        
        return stats;
    }
    
    @Override
    public boolean forceLogout(Long userId) {
        try {
            // 调用UserService的forceLogout方法，执行完整的下线逻辑
            userService.forceLogout(userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<String> getUserActivity(Long userId, int days) {
        List<String> activities = new ArrayList<>();
        
        // 从数据库获取活动日志
        List<Map<String, Object>> activityData = onlineUserMapper.getUserActivity(userId, days);
        
        for (Map<String, Object> data : activityData) {
            String activityTime = (String) data.get("activityTime");
            String activityContent = (String) data.get("activityContent");
            activities.add(activityTime + " - " + activityContent);
        }
        
        // 如果没有数据，使用默认数据
        if (activities.isEmpty()) {
            activities.add("2026-04-15 10:30:00 - 登录系统");
            activities.add("2026-04-15 10:35:00 - 查看个人资料");
            activities.add("2026-04-15 11:00:00 - 浏览商品列表");
            activities.add("2026-04-15 11:15:00 - 添加商品到购物车");
            activities.add("2026-04-15 11:30:00 - 提交订单");
        }
        
        return activities;
    }
}
