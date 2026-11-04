package com.minecraft.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    private String module;         // 业务模块

    private String bizId;          // 业务对象 ID

    private Integer status;        // 操作状态：0-失败，1-成功

    private LocalDateTime startTime; // 开始时间

    private LocalDateTime endTime;   // 结束时间

    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 200, message = "每页数量不能超过200")
    private Integer pageSize = 10;
}
