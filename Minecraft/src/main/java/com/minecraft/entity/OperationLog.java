package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体，对应 operation_logs 表（已按系统日志文档增强字段）。
 */
@Data
@TableName("operation_logs")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer userId;

    private String username;

    private String operationType;

    private String operationDesc;

    /** 业务对象 ID，如订单号、商品 ID、会员 ID */
    private String bizId;

    /** 操作详情，记录“改了什么”，例如“价格从 99.00 修改为 89.00” */
    private String detail;

    private String ipAddress;

    private String userAgent;

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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
