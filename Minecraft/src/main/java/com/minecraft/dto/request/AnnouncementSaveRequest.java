package com.minecraft.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告新建/编辑请求
 */
@Data
public class AnnouncementSaveRequest {
    /** 编辑时必填 */
    private Long id;

    @NotBlank(message = "公告标题不能为空")
    @Size(max = 200, message = "公告标题不能超过200字")
    private String title;

    @Size(max = 500, message = "公告摘要不能超过500字")
    private String summary;

    @NotBlank(message = "公告内容不能为空")
    private String content;

    /** 类型: 1-系统公告 2-活动通知 3-维护通知 4-版本更新 */
    private Integer type = 1;

    /** 级别: 1-普通 2-重要 3-紧急 */
    private Integer level = 1;

    private Long categoryId;

    private String coverImage;

    /** 展示方式: 1-仅列表 2-弹窗 3-轮播 4-顶部横幅 */
    private Integer displayMode = 1;

    /** 目标人群: 1-全体 2-登录用户 3-新用户 4-指定用户 */
    private Integer targetAudience = 1;

    /** 定向用户ID列表（targetAudience=4 时生效） */
    private List<Long> targetUserIds;

    /** 0-存为草稿 1-立即发布；定时发布：存草稿并设置未来 publishTime */
    private Integer status = 0;

    private Integer isTop = 0;

    private Integer sortWeight = 0;

    /** 定时发布时间（null 且 status=1 表示立即发布） */
    private LocalDateTime publishTime;

    /** 过期时间（null=永不过期） */
    private LocalDateTime expireTime;
}
