package com.minecraft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 公告状态枚举：草稿 → 已发布 → 已下架
 */
@Getter
@AllArgsConstructor
public enum AnnouncementStatusEnum {
    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    OFFLINE(2, "已下架");

    private final Integer code;
    private final String desc;
}
