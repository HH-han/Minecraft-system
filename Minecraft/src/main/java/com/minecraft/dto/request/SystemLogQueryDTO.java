package com.minecraft.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统日志查询条件。
 */
@Data
public class SystemLogQueryDTO {
    private String keyword;        // 关键字搜索（用户名、内容、模块）

    private Long userId;           // 用户ID

    private String logLevel;       // 日志级别：DEBUG, INFO, WARN, ERROR

    private String logType;        // 日志类型：OPERATION, SYSTEM, SECURITY, BUSINESS

    private String module;         // 模块名称

    private String status;         // 状态：SUCCESS, FAIL, ERROR

    private LocalDateTime startTime; // 开始时间

    private LocalDateTime endTime;   // 结束时间

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
