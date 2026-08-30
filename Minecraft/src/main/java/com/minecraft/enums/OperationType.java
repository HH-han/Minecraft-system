package com.minecraft.enums;

/**
 * 操作类型枚举，对应系统日志文档中 operation_logs.operation_type 字段。
 */
public enum OperationType {
    LOGIN("登录"),
    LOGOUT("登出"),
    ADD("新增"),
    UPDATE("更新"),
    DELETE("删除"),
    QUERY("查询"),
    EXPORT("导出"),
    IMPORT("导入"),
    OTHER("其他");

    private final String description;

    OperationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
