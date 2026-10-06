package com.minecraft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 公告类型枚举
 */
@Getter
@AllArgsConstructor
public enum AnnouncementTypeEnum {
    SYSTEM(1, "系统公告"),
    ACTIVITY(2, "活动通知"),
    MAINTENANCE(3, "维护通知"),
    VERSION(4, "版本更新");

    private final Integer code;
    private final String desc;
}
