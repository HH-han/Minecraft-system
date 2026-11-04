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
    /** 业务对象 ID */
    private String bizId;
    /** 操作详情（变更前后对比等） */
    private String detail;
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

    /** IP地址（与实体 ipAddress 对齐，便于前端直接展示） */
    private String ipAddress;
    /** 操作类型名称 */
    private String operation;
    /** 操作内容 */
    private String operationContent;
    /** 请求方法 */
    private String method;
    /** 请求参数 */
    private String params;
    /** 操作时间 */
    private LocalDateTime operationTime;
    /** 耗时（毫秒） */
    private Long timeCost;
}
