package com.minecraft.dto.request;

import lombok.Data;

/**
 * 公告分页查询请求（用户端与管理端共用）
 */
@Data
public class AnnouncementQueryRequest {
    /** 页码，从 1 开始 */
    private Integer page = 1;
    /** 每页数量 */
    private Integer size = 10;
    /** 类型: 1-系统公告 2-活动通知 3-维护通知 4-版本更新 */
    private Integer type;
    /** 级别: 1-普通 2-重要 3-紧急 */
    private Integer level;
    /** 展示方式: 1-仅列表 2-弹窗 3-轮播 4-顶部横幅 */
    private Integer displayMode;
    /** 状态: 0-草稿 1-已发布 2-已下架（仅管理端生效） */
    private Integer status;
    /** 标题/摘要关键词（仅管理端生效） */
    private String keyword;
}
