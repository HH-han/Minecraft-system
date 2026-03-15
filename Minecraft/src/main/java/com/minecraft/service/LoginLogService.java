package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.minecraft.entity.LoginLog;
import java.util.Map;

public interface LoginLogService extends IService<LoginLog> {
    IPage<LoginLog> getLoginLogPage(Integer pageNum, Integer pageSize, String keyword, String status, String startTime, String endTime);
    LoginLog getLoginLogById(Long id);
    Map<String, Object> getLoginLogStatistics();
    void blockIPAddress(String ip, Integer duration, String reason);
    boolean isIPBlocked(String ip);
    String getIPBlockReason(String ip);
    void clearLoginLogs(Long userId);
}