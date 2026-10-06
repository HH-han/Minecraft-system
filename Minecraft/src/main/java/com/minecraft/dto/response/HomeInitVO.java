package com.minecraft.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 首页合并初始化 VO：活跃公告 + 未读数（接口合并，减少首页请求数）
 */
@Data
public class HomeInitVO {
    private List<AnnouncementVO> activeList;
    private Long unreadCount;
}
