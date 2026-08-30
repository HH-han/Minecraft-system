package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.OperationLogQueryDTO;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.OperationLog;
import com.minecraft.vo.OperationLogVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志服务，对应 db_minecraft.sql 中的 operation_logs 表。
 */
public interface OperationLogService extends IService<OperationLog> {

    /**
     * 分页查询操作日志
     */
    PageResponse<OperationLogVO> getOperationLogList(OperationLogQueryDTO queryDTO);

    /**
     * 获取操作日志详情
     */
    OperationLogVO getOperationLogDetail(Long id);

    /**
     * 根据用户ID获取操作日志
     */
    List<OperationLogVO> getLogsByUserId(Integer userId);

    /**
     * 根据用户ID查询最近的操作日志
     */
    List<OperationLog> getRecentLogsByUserId(Integer userId, Integer limit);

    /**
     * 统计指定时间范围内的操作次数
     */
    Long countByTimeRange(String startTime, String endTime);

    /**
     * 保存操作日志
     */
    void saveOperationLog(OperationLog operationLog);

    /**
     * 异步保存操作日志
     */
    void saveOperationLogAsync(OperationLog operationLog);

    /**
     * 删除操作日志
     */
    void deleteOperationLog(Long id);

    /**
     * 批量删除指定时间之前的操作日志
     */
    boolean deleteLogsBeforeTime(LocalDateTime time);
}
