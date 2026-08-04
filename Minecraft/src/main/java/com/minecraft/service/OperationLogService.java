package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.OperationLog;

import java.util.List;

/**
 * 操作日志服务，对应 db_minecraft.sql.sql 中的 operation_logs 表。
 */
public interface OperationLogService extends IService<OperationLog> {

    PageResponse<OperationLog> getOperationLogList(PageRequest request);

    OperationLog getOperationLogDetail(Long id);

    List<OperationLog> getLogsByUserId(Integer userId);

    void saveOperationLog(OperationLog operationLog);

    void deleteOperationLog(Long id);
}
