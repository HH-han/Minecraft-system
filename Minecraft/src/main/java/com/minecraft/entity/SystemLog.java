package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统日志实体，对应 system_log 表，用于系统报错、系统信息、安全事件等详细日志追踪。
 */
@Data
@TableName("system_log")
public class SystemLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String traceId;

    private String spanId;

    private Long userId;

    private String username;

    private String userIp;

    private String userAgent;

    private String requestId;

    private String logLevel;

    private String logType;

    private String module;

    private String action;

    private String content;

    private String requestUrl;

    private String requestMethod;

    private String requestParams;

    private String responseData;

    private Long executionTime;

    private String status;

    private String errorStack;

    private String serverHost;

    private Integer serverPort;

    private LocalDateTime createdAt;
}
