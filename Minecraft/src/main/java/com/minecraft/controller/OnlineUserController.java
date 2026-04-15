package com.minecraft.controller;

import com.minecraft.common.Result;
import com.minecraft.service.OnlineUserService;
import com.minecraft.vo.OnlineUserVO;
import com.minecraft.vo.UserStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 在线用户监控控制器
 */
@RestController
@RequestMapping("/api/online-users")
public class OnlineUserController {
    
    @Autowired
    private OnlineUserService onlineUserService;
    
    /**
     * 获取在线用户列表
     * @param page 页码
     * @param size 每页大小
     * @return 在线用户列表
     */
    @GetMapping
    public Result getOnlineUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<OnlineUserVO> users = onlineUserService.getOnlineUsers(page, size);
        return Result.success(users);
    }
    
    /**
     * 获取用户统计信息
     * @return 用户统计信息
     */
    @GetMapping("/stats")
    public Result getUserStats() {
        UserStatsVO stats = onlineUserService.getUserStats();
        return Result.success(stats);
    }
    
    /**
     * 强制用户下线
     * @param userId 用户ID
     * @return 是否成功
     */
    @PostMapping("/logout/{userId}")
    public Result forceLogout(@PathVariable Long userId) {
        boolean success = onlineUserService.forceLogout(userId);
        return Result.success(success);
    }
    
    /**
     * 获取用户活动日志
     * @param userId 用户ID
     * @param days 天数
     * @return 活动日志
     */
    @GetMapping("/activity")
    public Result getUserActivity(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "7") int days) {
        List<String> activities = onlineUserService.getUserActivity(userId, days);
        return Result.success(activities);
    }
}
