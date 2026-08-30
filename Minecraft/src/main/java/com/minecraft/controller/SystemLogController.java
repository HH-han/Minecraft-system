package com.minecraft.controller;

import com.minecraft.dto.request.SystemLogQueryDTO;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.SystemLog;
import com.minecraft.service.SystemLogService;
import com.minecraft.vo.SystemLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 系统日志管理接口：查询系统报错、系统信息等日志记录。
 */
@Tag(name = "系统日志管理")
@RestController
@RequestMapping("/api/system-logs")
public class SystemLogController {

    @Autowired
    private SystemLogService systemLogService;

    @Operation(summary = "获取系统日志列表(分页)")
    @GetMapping("/list")
    public ApiResponse<PageResponse<SystemLogVO>> getSystemLogList(SystemLogQueryDTO queryDTO) {
        return ApiResponse.success(systemLogService.getSystemLogList(queryDTO));
    }

    @Operation(summary = "获取系统日志详情")
    @GetMapping("/{id}")
    public ApiResponse<SystemLogVO> getSystemLogDetail(@PathVariable Long id) {
        return ApiResponse.success(systemLogService.getSystemLogDetail(id));
    }

    @Operation(summary = "记录系统日志")
    @PostMapping("/add")
    public ApiResponse<Void> saveSystemLog(@RequestBody SystemLog systemLog) {
        systemLogService.saveSystemLog(systemLog);
        return ApiResponse.success("记录成功", null);
    }

    @Operation(summary = "删除系统日志")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteSystemLog(@PathVariable Long id) {
        systemLogService.deleteSystemLog(id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "删除指定时间之前的系统日志")
    @DeleteMapping("/before")
    public ApiResponse<Void> deleteLogsBeforeTime(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime time) {
        systemLogService.deleteLogsBeforeTime(time);
        return ApiResponse.success("清理成功", null);
    }
}
