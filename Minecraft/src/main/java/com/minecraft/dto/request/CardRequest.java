package com.minecraft.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 旅行卡片创建/更新请求
 */
@Data
public class CardRequest {
    /**
     * 卡片ID（更新时必填）
     */
    private Integer id;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题最长不能超过200个字符")
    private String title;

    /**
     * 内容
     */
    @NotBlank(message = "内容不能为空")
    private String content;

    /**
     * 图片URL数组（JSON格式字符串，如 ["http://...","http://..."]）
     */
    private String images;

    /**
     * 地点
     */
    @Size(max = 255, message = "地点最长不能超过255个字符")
    private String location;

    /**
     * 标签，多个标签用逗号分隔
     */
    @Size(max = 500, message = "标签最长不能超过500个字符")
    private String tags;

    /**
     * 关联标签ID列表（维护 card_tags 关联表）
     */
    private List<Integer> tagIds;

    /**
     * 旅行日期，格式：yyyy-MM-dd
     */
    private LocalDate date;
}
