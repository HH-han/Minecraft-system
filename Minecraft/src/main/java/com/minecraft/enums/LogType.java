package com.minecraft.enums;

/**
 * 系统日志类型枚举，对应 system_log.log_type 字段。
 */
public enum LogType {
    OPERATION("操作日志"),
    SYSTEM("系统日志"),
    SECURITY("安全日志"),
    BUSINESS("业务日志");

    private final String description;

    LogType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
