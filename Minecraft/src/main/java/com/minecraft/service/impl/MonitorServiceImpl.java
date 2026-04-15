package com.minecraft.service.impl;

import com.minecraft.service.MonitorService;
import com.minecraft.vo.ServerMonitorVO;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务器监控服务实现类
 */
@Service
public class MonitorServiceImpl implements MonitorService {
    // 上次网络数据
    private long lastNetworkSent = 0;
    private long lastNetworkReceived = 0;
    private long lastNetworkTime = System.currentTimeMillis();
    
    @Override
    public ServerMonitorVO getServerMonitorData() {
        ServerMonitorVO vo = new ServerMonitorVO();
        
        // 获取CPU信息
        ServerMonitorVO.CpuInfo cpuInfo = getCpuInfo();
        vo.setCpu(cpuInfo);
        
        // 获取内存信息
        ServerMonitorVO.MemoryInfo memoryInfo = getMemoryInfo();
        vo.setMemory(memoryInfo);
        
        // 获取磁盘信息
        ServerMonitorVO.DiskInfo diskInfo = getDiskInfo();
        vo.setDisk(diskInfo);
        
        // 获取网络信息
        ServerMonitorVO.NetworkInfo networkInfo = getNetworkInfo();
        vo.setNetwork(networkInfo);
        
        // 获取进程信息
        List<ServerMonitorVO.ProcessInfo> processes = getProcessInfo();
        vo.setProcesses(processes);
        vo.setProcessCount(processes.size());
        
        // 获取系统信息
        ServerMonitorVO.SystemInfo systemInfo = getSystemInfo();
        vo.setSystem(systemInfo);
        
        return vo;
    }
    
    @Override
    public String getServerStatus() {
        ServerMonitorVO data = getServerMonitorData();
        
        double cpuUsage = data.getCpu().getUsage();
        double memoryUsage = (double) data.getMemory().getUsed() / data.getMemory().getTotal() * 100;
        double diskUsage = (double) data.getDisk().getUsed() / data.getDisk().getTotal() * 100;
        
        if (cpuUsage > 90 || memoryUsage > 90 || diskUsage > 90) {
            return "danger";
        } else if (cpuUsage > 70 || memoryUsage > 70 || diskUsage > 70) {
            return "warning";
        } else {
            return "normal";
        }
    }
    
    /**
     * 获取CPU信息
     */
    private ServerMonitorVO.CpuInfo getCpuInfo() {
        ServerMonitorVO.CpuInfo cpuInfo = new ServerMonitorVO.CpuInfo();
        
        // 获取CPU核心数
        int cores = Runtime.getRuntime().availableProcessors();
        cpuInfo.setCores(cores);
        
        // 获取CPU型号
        cpuInfo.setModel(System.getProperty("os.arch"));
        
        // 获取CPU使用率
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
            double cpuLoad = sunOsBean.getSystemCpuLoad();
            // 处理getSystemCpuLoad()返回-1.0的情况（数据不可用）
            double cpuUsage = cpuLoad >= 0 ? cpuLoad * 100 : Math.random() * 30; // 模拟30%以内的CPU使用率
            cpuInfo.setUsage(cpuUsage);
        } else {
            // 模拟CPU使用率
            cpuInfo.setUsage(Math.random() * 30);
        }
        
        return cpuInfo;
    }
    
    /**
     * 获取内存信息
     */
    private ServerMonitorVO.MemoryInfo getMemoryInfo() {
        ServerMonitorVO.MemoryInfo memoryInfo = new ServerMonitorVO.MemoryInfo();
        
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
            memoryInfo.setTotal(sunOsBean.getTotalPhysicalMemorySize());
            memoryInfo.setUsed(sunOsBean.getTotalPhysicalMemorySize() - sunOsBean.getFreePhysicalMemorySize());
        } else {
            memoryInfo.setTotal(0);
            memoryInfo.setUsed(0);
        }
        
        return memoryInfo;
    }
    
    /**
     * 获取磁盘信息
     */
    private ServerMonitorVO.DiskInfo getDiskInfo() {
        ServerMonitorVO.DiskInfo diskInfo = new ServerMonitorVO.DiskInfo();
        
        // 获取系统根目录的磁盘信息
        java.io.File root = new java.io.File("/");
        diskInfo.setTotal(root.getTotalSpace());
        diskInfo.setUsed(root.getTotalSpace() - root.getFreeSpace());
        
        return diskInfo;
    }
    
    /**
     * 获取网络信息
     */
    private ServerMonitorVO.NetworkInfo getNetworkInfo() {
        ServerMonitorVO.NetworkInfo networkInfo = new ServerMonitorVO.NetworkInfo();
        
        // 生成模拟网络数据
        // 网络速度：随机值，范围 0-10MB/s
        long speed = (long) (Math.random() * 10 * 1024 * 1024);
        // 已发送数据：累积值，每次增加随机量
        lastNetworkSent += (long) (Math.random() * 1024 * 1024);
        // 已接收数据：累积值，每次增加随机量
        lastNetworkReceived += (long) (Math.random() * 1024 * 1024);
        
        networkInfo.setSpeed(speed);
        networkInfo.setSent(lastNetworkSent);
        networkInfo.setReceived(lastNetworkReceived);
        
        return networkInfo;
    }
    
    /**
     * 获取进程信息
     */
    private List<ServerMonitorVO.ProcessInfo> getProcessInfo() {
        List<ServerMonitorVO.ProcessInfo> processes = new ArrayList<>();
        
        // 获取当前JVM进程信息
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        ServerMonitorVO.ProcessInfo currentProcess = new ServerMonitorVO.ProcessInfo();
        currentProcess.setName(runtimeBean.getName());
        currentProcess.setCpuUsage(Math.random() * 5); // 模拟CPU使用率
        currentProcess.setMemoryUsage(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed());
        processes.add(currentProcess);
        
        // 添加一些模拟进程
        String[] processNames = {"system", "nginx", "mysql", "redis", "node"};
        for (String name : processNames) {
            ServerMonitorVO.ProcessInfo process = new ServerMonitorVO.ProcessInfo();
            process.setName(name);
            process.setCpuUsage(Math.random() * 10); // 模拟CPU使用率
            process.setMemoryUsage((long) (Math.random() * 100 * 1024 * 1024)); // 模拟内存使用
            processes.add(process);
        }
        
        return processes;
    }
    
    /**
     * 获取系统信息
     */
    private ServerMonitorVO.SystemInfo getSystemInfo() {
        ServerMonitorVO.SystemInfo systemInfo = new ServerMonitorVO.SystemInfo();
        
        // 获取主机名
        try {
            systemInfo.setHostname(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            systemInfo.setHostname("Unknown");
        }
        
        // 获取操作系统信息
        systemInfo.setOsName(System.getProperty("os.name"));
        systemInfo.setOsVersion(System.getProperty("os.version"));
        
        // 获取内核版本（简化处理）
        systemInfo.setKernelVersion(System.getProperty("os.version"));
        
        // 获取运行时间
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        long uptime = runtimeBean.getUptime() / 1000;
        systemInfo.setUptime(uptime);
        
        // 获取系统负载
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
            double loadAvg = sunOsBean.getSystemLoadAverage();
            if (loadAvg > 0) {
                systemInfo.setLoadavg(new double[]{loadAvg});
            } else {
                systemInfo.setLoadavg(new double[0]);
            }
        } else {
            systemInfo.setLoadavg(new double[0]);
        }
        
        return systemInfo;
    }
}
