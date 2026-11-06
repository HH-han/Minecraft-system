package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * IM「服务」配置项（服务页宫格）
 */
@Data
@TableName("service_item")
public class ServiceItem {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 唯一标识 key */
    private String itemKey;

    /** 名称（中文） */
    private String name;

    /** 名称（英文） */
    private String nameEn;

    /** 图标名（对应前端 Icon 组件） */
    private String icon;

    /** 分组 finance/life/travel/shopping */
    private String category;

    /** 跳转类型 none/route/url/wallet */
    private String linkType;

    /** 跳转地址/钱包动作 */
    private String link;

    /** 组内排序 */
    private Integer sort;

    /** 状态 0-下架 1-正常 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
