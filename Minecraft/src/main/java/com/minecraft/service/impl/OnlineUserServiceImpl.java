package com.minecraft.service.impl;

import com.minecraft.service.OnlineUserService;
import com.minecraft.vo.OnlineUserVO;
import com.minecraft.vo.UserStatsVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 在线用户监控服务实现类
 */
@Service
public class OnlineUserServiceImpl implements OnlineUserService {
    
    // 模拟在线用户存储
    private final ConcurrentHashMap<Long, OnlineUserVO> onlineUsers = new ConcurrentHashMap<>();
    
    public OnlineUserServiceImpl() {
        // 初始化一些模拟在线用户数据
        initializeOnlineUsers();
    }
    
    private void initializeOnlineUsers() {
        // 模拟用户1
        OnlineUserVO user1 = new OnlineUserVO();
        user1.setId(1L);
        user1.setUsername("admin");
        user1.setNickname("管理员");
        user1.setRole("admin");
        user1.setLoginIp("192.168.1.100");
        user1.setLoginTime(new Date(System.currentTimeMillis() - 3600000)); // 1小时前
        user1.setLastActivityTime(new Date());
        user1.setBrowser("Chrome");
        user1.setOs("Windows 10");
        user1.setStatus("online");
        onlineUsers.put(user1.getId(), user1);
        
        // 模拟用户2
        OnlineUserVO user2 = new OnlineUserVO();
        user2.setId(2L);
        user2.setUsername("user1");
        user2.setNickname("普通用户");
        user2.setRole("user");
        user2.setLoginIp("192.168.1.101");
        user2.setLoginTime(new Date(System.currentTimeMillis() - 7200000)); // 2小时前
        user2.setLastActivityTime(new Date(System.currentTimeMillis() - 600000)); // 10分钟前
        user2.setBrowser("Firefox");
        user2.setOs("macOS");
        user2.setStatus("idle");
        onlineUsers.put(user2.getId(), user2);
        
        // 模拟用户3
        OnlineUserVO user3 = new OnlineUserVO();
        user3.setId(3L);
        user3.setUsername("user2");
        user3.setNickname("测试用户");
        user3.setRole("user");
        user3.setLoginIp("192.168.1.102");
        user3.setLoginTime(new Date(System.currentTimeMillis() - 1800000)); // 30分钟前
        user3.setLastActivityTime(new Date(System.currentTimeMillis() - 300000)); // 5分钟前
        user3.setBrowser("Safari");
        user3.setOs("iOS");
        user3.setStatus("online");
        onlineUsers.put(user3.getId(), user3);
        
        // 模拟用户4
        OnlineUserVO user4 = new OnlineUserVO();
        user4.setId(4L);
        user4.setUsername("user3");
        user4.setNickname("新用户");
        user4.setRole("user");
        user4.setLoginIp("192.168.1.103");
        user4.setLoginTime(new Date(System.currentTimeMillis() - 900000)); // 15分钟前
        user4.setLastActivityTime(new Date(System.currentTimeMillis() - 120000)); // 2分钟前
        user4.setBrowser("Edge");
        user4.setOs("Windows 11");
        user4.setStatus("online");
        onlineUsers.put(user4.getId(), user4);
        
        // 模拟用户5
        OnlineUserVO user5 = new OnlineUserVO();
        user5.setId(5L);
        user5.setUsername("user4");
        user5.setNickname("VIP用户");
        user5.setRole("vip");
        user5.setLoginIp("192.168.1.104");
        user5.setLoginTime(new Date(System.currentTimeMillis() - 2700000)); // 45分钟前
        user5.setLastActivityTime(new Date(System.currentTimeMillis() - 180000)); // 3分钟前
        user5.setBrowser("Chrome");
        user5.setOs("Linux");
        user5.setStatus("idle");
        onlineUsers.put(user5.getId(), user5);
    }
    
    @Override
    public List<OnlineUserVO> getOnlineUsers(int page, int size) {
        List<OnlineUserVO> users = new ArrayList<>(onlineUsers.values());
        // 简单的分页处理
        int start = (page - 1) * size;
        int end = Math.min(start + size, users.size());
        if (start >= users.size()) {
            return new ArrayList<>();
        }
        return users.subList(start, end);
    }
    
    @Override
    public UserStatsVO getUserStats() {
        UserStatsVO stats = new UserStatsVO();
        stats.setOnlineCount(onlineUsers.size());
        stats.setTodayLoginCount(23); // 模拟今日登录次数
        stats.setTotalCount(1000); // 模拟总用户数
        stats.setActiveCount(150); // 模拟活跃用户数
        stats.setAvgOnlineTime(45.5); // 模拟平均在线时长
        
        // 模拟登录趋势
        UserStatsVO.LoginTrendVO trend = new UserStatsVO.LoginTrendVO();
        trend.setTimePoints(new String[]{"00:00", "04:00", "08:00", "12:00", "16:00", "20:00"});
        trend.setLoginCounts(new int[]{5, 2, 15, 25, 30, 18});
        stats.setLoginTrend(trend);
        
        return stats;
    }
    
    @Override
    public boolean forceLogout(Long userId) {
        return onlineUsers.remove(userId) != null;
    }
    
    @Override
    public List<String> getUserActivity(Long userId, int days) {
        List<String> activities = new ArrayList<>();
        
        // 模拟用户活动日志
        activities.add("2026-04-15 10:30:00 - 登录系统");
        activities.add("2026-04-15 10:35:00 - 查看个人资料");
        activities.add("2026-04-15 11:00:00 - 浏览商品列表");
        activities.add("2026-04-15 11:15:00 - 添加商品到购物车");
        activities.add("2026-04-15 11:30:00 - 提交订单");
        
        return activities;
    }
}
