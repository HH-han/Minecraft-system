package com.minecraft.controller;

import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.OperationLog;
import com.minecraft.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "操作日志管理")
@RestController
@RequestMapping("/api/operation-logs")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @Operation(summary = "获取操作日志列表(分页)")
    @GetMapping("/list")
    public ApiResponse<PageResponse<OperationLog>> getOperationLogList(PageRequest request) {
        return ApiResponse.success(operationLogService.getOperationLogList(request));
    }

    @Operation(summary = "获取操作日志详情")
    @GetMapping("/{id}")
    public ApiResponse<OperationLog> getOperationLogDetail(@PathVariable Long id) {
        return ApiResponse.success(operationLogService.getOperationLogDetail(id));
    }

    @Operation(summary = "根据用户ID获取操作日志")
    @GetMapping("/user/{userId}")
    public ApiResponse<List<OperationLog>> getLogsByUserId(@PathVariable Integer userId) {
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
}
