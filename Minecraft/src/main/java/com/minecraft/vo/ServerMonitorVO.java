package com.minecraft.vo;

import lombok.Data;

import java.util.List;

/**
 * 服务器监控数据VO
 */
@Data
public class ServerMonitorVO {
    /**
     * CPU信息
     */
    private CpuInfo cpu;
    
    /**
     * 内存信息
     */
    private MemoryInfo memory;
    
    /**
     * 磁盘信息
     */
    private DiskInfo disk;
    
    /**
     * 网络信息
     */
    private NetworkInfo network;
    
    /**
     * 进程信息
     */
    private List<ProcessInfo> processes;
    
    /**
     * 进程数量
     */
    private int processCount;
    
    /**
     * 系统信息
     */
    private SystemInfo system;
    
    /**
     * CPU信息内部类
     */
    @Data
    public static class CpuInfo {
        /**
         * CPU使用率
         */
        private double usage;
        
        /**
         * CPU核心数
         */
        private int cores;
        
        /**
         * CPU型号
         */
        private String model;
    }
    
    /**
     * 内存信息内部类
     */
    @Data
    public static class MemoryInfo {
        /**
         * 已使用内存（字节）
         */
        private long used;
        
        /**
         * 总内存（字节）
         */
        private long total;
    }
    
    /**
     * 磁盘信息内部类
     */
    @Data
    public static class DiskInfo {
        /**
         * 已使用磁盘空间（字节）
         */
        private long used;
        
        /**
         * 总磁盘空间（字节）
         */
        private long total;
    }
    
    /**
     * 网络信息内部类
     */
    @Data
    public static class NetworkInfo {
        /**
         * 网络速度（字节/秒）
         */
        private long speed;
        
        /**
         * 已发送数据（字节）
         */
        private long sent;
        
        /**
         * 已接收数据（字节）
         */
        private long received;
    }
    
    /**
     * 进程信息内部类
     */
    @Data
    public static class ProcessInfo {
        /**
         * 进程名称
         */
        private String name;
        
        /**
         * CPU使用率（百分比）
         */
        private double cpuUsage;
        
        /**
         * 内存使用率（字节）
         */
        private long memoryUsage;
    }
    
    /**
     * 系统信息内部类
     */
    @Data
    public static class SystemInfo {
        /**
         * 主机名
         */
        private String hostname;
        
        /**
         * 操作系统名称
         */
        private String osName;
        
        /**
         * 操作系统版本
         */
        private String osVersion;
        
        /**
         * 内核版本
         */
        private String kernelVersion;
        
        /**
         * 运行时间（秒）
         */
        private long uptime;
        
        /**
         * 系统负载
         */
        private double[] loadavg;
    }
}
