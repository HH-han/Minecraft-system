package com.minecraft.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.LoginLog;
import com.minecraft.service.LoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "登录日志管理")
@RestController
@RequestMapping("/api/login-log")
public class LoginLogController {

    @Autowired
    private LoginLogService loginLogService;

    @Operation(summary ="获取登录日志列表")
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getLoginLogs(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        IPage<LoginLog> page = loginLogService.getLoginLogPage(pageNum, pageSize, keyword, status, startTime, endTime);
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("list", page.getRecords());
        result.put("total", page.getTotal());
        result.put("statistics", loginLogService.getLoginLogStatistics());
        return ApiResponse.success(result);
    }

    @Operation(summary ="获取登录日志详情")
    @GetMapping("/{id}")
    public ApiResponse<LoginLog> getLoginLogDetail(@PathVariable Long id) {
        LoginLog loginLog = loginLogService.getLoginLogById(id);
        return ApiResponse.success(loginLog);
    }

    @Operation(summary ="封禁IP地址")
    @PostMapping("/block-ip")
    public ApiResponse<Void> blockIPAddress(
            @RequestParam String ip,
            @RequestParam Integer duration,
            @RequestParam(required = false) String reason) {
        loginLogService.blockIPAddress(ip, duration, reason);
        return ApiResponse.success("IP封禁成功", null);
    }
}