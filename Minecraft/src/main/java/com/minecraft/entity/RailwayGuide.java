package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 铁路出行指南
 */
@Data
@TableName("railway_guide")
public class RailwayGuide {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** TICKET / SAFETY / SERVICE / SCENERY / DINING */
    private String category;

    private String title;

    private String summary;

    private String content;

    /** 逗号分隔标签 */
    private String tags;

    /** 封面配色 blue / orange / mint / violet */
    private String coverTone;

    private Integer views;

    private Integer sort;

    private Integer status;

    private LocalDateTime publishTime;

    private LocalDateTime createTime;
}
