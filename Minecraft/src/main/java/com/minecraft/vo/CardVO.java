package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 旅行卡片展示对象
 */
@Data
public class CardVO {
    /**
     * 卡片ID
     */
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 关联用户ID
     */
    private Integer userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 图片URL数组原始JSON字符串
     */
    private String images;

    /**
     * 解析后的图片URL列表
     */
    private List<String> imageList;

    /**
     * 地点
     */
    private String location;

    /**
     * 标签，多个标签用逗号分隔
     */
    private String tags;

    /**
     * 关联标签名称列表
     */
    private List<String> tagNames;

    /**
     * 旅行日期
     */
    private LocalDate date;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 状态：1-正常，0-删除，2-审核中
     */
    private Integer status;
}
