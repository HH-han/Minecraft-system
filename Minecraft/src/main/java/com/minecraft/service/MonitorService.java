package com.minecraft.service;

import com.minecraft.vo.ServerMonitorVO;

/**
 * 服务器监控服务接口
 */
public interface MonitorService {
    /**
     * 获取服务器监控数据
     * @return 服务器监控数据
     */
    ServerMonitorVO getServerMonitorData();
    
    /**
     * 获取服务器状态
     * @return 服务器状态
     */
    String getServerStatus();
}
