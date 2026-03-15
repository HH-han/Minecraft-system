package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minecraft.entity.LoginLog;
import com.minecraft.mapper.LoginLogMapper;
import com.minecraft.service.LoginLogService;
import com.minecraft.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

    @Autowired
    private LoginLogMapper loginLogMapper;
    
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public IPage<LoginLog> getLoginLogPage(Integer pageNum, Integer pageSize, String keyword, String status, String startTime, String endTime) {
        Page<LoginLog> page = new Page<>(pageNum, pageSize);
        return loginLogMapper.selectLoginLogPage(page, keyword, status, startTime, endTime);
    }

    @Override
    public LoginLog getLoginLogById(Long id) {
        return getById(id);
    }

    @Override
    public Map<String, Object> getLoginLogStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 统计总登录次数
        long total = count();
        statistics.put("total", total);
        
        // 统计成功登录次数
        LambdaQueryWrapper<LoginLog> successWrapper = new LambdaQueryWrapper<>();
        successWrapper.eq(LoginLog::getStatus, "1");
        long success = count(successWrapper);
        statistics.put("success", success);
        
        // 统计失败登录次数
        long fail = total - success;
        statistics.put("fail", fail);
        
        // 统计独立用户数
        LambdaQueryWrapper<LoginLog> uniqueWrapper = new LambdaQueryWrapper<>();
        uniqueWrapper.select(LoginLog::getUserId).groupBy(LoginLog::getUserId);
        long uniqueUsers = count(uniqueWrapper);
        statistics.put("uniqueUsers", uniqueUsers);
        
        return statistics;
    }

    @Override
    public void blockIPAddress(String ip, Integer duration, String reason) {
        // 这里实现IP封禁逻辑
        // 可以创建一个IP封禁表来存储封禁信息
        // 暂时使用Redis来存储封禁信息
        String blockKey = "block:ip:" + ip;
        redisUtil.set(blockKey, reason, duration, TimeUnit.HOURS);
    }

    /**
     * 检查IP是否被封禁
     * @param ip IP地址
     * @return 是否被封禁
     */
    public boolean isIPBlocked(String ip) {
        String blockKey = "block:ip:" + ip;
        return redisUtil.exists(blockKey);
    }

    /**
     * 获取IP封禁原因
     * @param ip IP地址
     * @return 封禁原因
     */
    public String getIPBlockReason(String ip) {
        String blockKey = "block:ip:" + ip;
        Object value = redisUtil.get(blockKey);
        return value != null ? value.toString() : null;
    }
}