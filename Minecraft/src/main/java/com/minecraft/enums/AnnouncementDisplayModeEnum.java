package com.minecraft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 公告展示方式枚举
 */
@Getter
@AllArgsConstructor
public enum AnnouncementDisplayModeEnum {
    LIST(1, "仅列表"),
    POPUP(2, "弹窗"),
    CAROUSEL(3, "轮播"),
    BANNER(4, "顶部横幅");

    private final Integer code;
    private final String desc;
}
