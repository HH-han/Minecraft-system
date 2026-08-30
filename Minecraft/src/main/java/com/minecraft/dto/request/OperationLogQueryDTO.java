package com.minecraft.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志查询条件，对应系统日志文档中的查询接口入参。
 */
@Data
public class OperationLogQueryDTO {
    private String keyword;        // 关键字搜索（用户名、描述、URI）

    private Integer userId;        // 用户ID

    private String operationType;  // 操作类型

    private Integer status;        // 操作状态：0-失败，1-成功

    private LocalDateTime startTime; // 开始时间

    private LocalDateTime endTime;   // 结束时间

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
