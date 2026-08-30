package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.SystemLogQueryDTO;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.SystemLog;
import com.minecraft.vo.SystemLogVO;

import java.time.LocalDateTime;

/**
 * 系统日志服务，对应 system_log 表，负责系统报错、系统信息、安全事件等日志的记录与查询。
 */
public interface SystemLogService extends IService<SystemLog> {

    /**
     * 分页查询系统日志
     */
    PageResponse<SystemLogVO> getSystemLogList(SystemLogQueryDTO queryDTO);

    /**
     * 获取系统日志详情
     */
    SystemLogVO getSystemLogDetail(Long id);

    /**
     * 保存系统日志
     */
    void saveSystemLog(SystemLog systemLog);

    /**
     * 异步保存系统日志
     */
    void saveSystemLogAsync(SystemLog systemLog);

    /**
     * 记录系统错误日志（自动补充请求上下文与错误堆栈）
     */
    void recordError(String module, String action, String content, Throwable e);

    /**
     * 记录系统信息日志（自动补充请求上下文）
     */
    void recordInfo(String module, String action, String content);

    /**
     * 删除系统日志
     */
    void deleteSystemLog(Long id);

    /**
     * 批量删除指定时间之前的系统日志
     */
    boolean deleteLogsBeforeTime(LocalDateTime time);
}
