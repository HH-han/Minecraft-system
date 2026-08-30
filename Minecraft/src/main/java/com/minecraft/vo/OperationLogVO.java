package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志视图对象，对应系统日志文档中的返回结构。
 */
@Data
public class OperationLogVO {
    private Long id;
    private Integer userId;
    private String username;
    private String userIp;
    private String operationType;
    private String operationDesc;
    private String requestUri;
    private String requestMethod;
    private String requestParams;
    private String responseResult;
    private Long executionTime;
    private Integer status;
    private String errorMsg;
    private String module;
    private String browser;
    private String os;
    private LocalDateTime createdAt;
}
