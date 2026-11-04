package com.minecraft.log;

import com.minecraft.entity.OperationLog;
import com.minecraft.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 操作日志落库组件：异步执行，避免插入日志影响业务响应时间。
 *
 * <p>真正的写入由 {@link OperationLogService#saveLogInNewTransaction} 以独立事务完成，
 * 因此即使业务事务回滚，失败操作日志仍然会被保留。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OperationLogRecorder {

    private final OperationLogService operationLogService;

    @Async("taskExecutor")
    public void record(OperationLog operationLog) {
        try {
            operationLogService.saveLogInNewTransaction(operationLog);
        } catch (Exception e) {
            log.error("异步保存操作日志失败", e);
        }
    }
}
