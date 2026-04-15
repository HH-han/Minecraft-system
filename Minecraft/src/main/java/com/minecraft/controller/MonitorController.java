package com.minecraft.controller;

import com.minecraft.common.Result;
import com.minecraft.service.MonitorService;
import com.minecraft.vo.ServerMonitorVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器监控控制器
 */
@RestController
@RequestMapping("/api/server-monitor")
public class MonitorController {
    
    @Autowired
    private MonitorService monitorService;
    
    /**
     * 获取服务器监控数据
     * @return 服务器监控数据
     */
    @GetMapping
    public Result getServerMonitor() {
        ServerMonitorVO data = monitorService.getServerMonitorData();
        return Result.success(data);
    }
    
    /**
     * 获取服务器状态
     * @return 服务器状态
     */
    @GetMapping("/status")
    public Result getServerStatus() {
        String status = monitorService.getServerStatus();
        return Result.success(status);
    }
}
