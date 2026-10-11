package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 旅游笔记
 */
@Data
@TableName("travel_note")
public class TravelNote {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String summary;

    private String coverUrl;

    private String content;

    private Integer contentType;

    private String destination;

    private Long continentId;

    private Long countryId;

    private Long cityId;

    private Integer travelDays;

    private LocalDate travelDate;

    private BigDecimal cost;

    /**
     * 0-草稿 1-待审 2-已发布 3-驳回 4-下架 5-私密
     */
    private Integer status;

    private Integer isTop;

    private Integer isFeatured;

    private Integer viewCount;

    private Integer likeCount;

    private Integer collectCount;

    private Integer commentCount;

    private Integer shareCount;

    private String rejectReason;

    private LocalDateTime publishTime;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
