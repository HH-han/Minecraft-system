package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统日志视图对象。
 */
@Data
public class SystemLogVO {
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
