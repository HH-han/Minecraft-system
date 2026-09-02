package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 旅行卡片实体类，对应数据库 cards 表
 */
@Data
@TableName("cards")
public class Card {
    @TableId(type = IdType.AUTO)
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
     * 图片URL数组（存储为JSON格式字符串）
     */
    private String images;

    /**
     * 地点
     */
    private String location;

    /**
     * 标签，多个标签用逗号分隔
     */
    private String tags;

    /**
     * 旅行日期（date 为 MySQL 关键字，使用反引号包裹）
     */
    @TableField("`date`")
    private LocalDate date;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
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
