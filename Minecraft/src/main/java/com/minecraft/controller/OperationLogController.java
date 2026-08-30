package com.minecraft.controller;

import com.minecraft.dto.request.OperationLogQueryDTO;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.OperationLog;
import com.minecraft.service.OperationLogService;
import com.minecraft.vo.OperationLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "操作日志管理")
@RestController
@RequestMapping("/api/operation-logs")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @Operation(summary = "获取操作日志列表(分页)")
    @GetMapping("/list")
    public ApiResponse<PageResponse<OperationLogVO>> getOperationLogList(OperationLogQueryDTO queryDTO) {
        return ApiResponse.success(operationLogService.getOperationLogList(queryDTO));
    }

    @Operation(summary = "获取操作日志详情")
    @GetMapping("/{id}")
    public ApiResponse<OperationLogVO> getOperationLogDetail(@PathVariable Long id) {
        return ApiResponse.success(operationLogService.getOperationLogDetail(id));
    }

    @Operation(summary = "根据用户ID获取操作日志")
    @GetMapping("/user/{userId}")
    public ApiResponse<List<OperationLogVO>> getLogsByUserId(@PathVariable Integer userId) {
        return ApiResponse.success(operationLogService.getLogsByUserId(userId));
    }

    @Operation(summary = "记录操作日志")
    @PostMapping("/add")
    public ApiResponse<Void> saveOperationLog(@RequestBody OperationLog operationLog) {
        operationLogService.saveOperationLog(operationLog);
        return ApiResponse.success("记录成功", null);
    }

    @Operation(summary = "删除操作日志")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteOperationLog(@PathVariable Long id) {
        operationLogService.deleteOperationLog(id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "删除指定时间之前的操作日志")
    @DeleteMapping("/before")
    public ApiResponse<Void> deleteLogsBeforeTime(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime time) {
        operationLogService.deleteLogsBeforeTime(time);
        return ApiResponse.success("清理成功", null);
    }
}
