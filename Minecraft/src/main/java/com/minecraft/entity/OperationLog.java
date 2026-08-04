package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("operation_logs")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer userId;

    private String operationType;

    private String operationDesc;

    private String ipAddress;

    private String userAgent;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
