package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 旅游笔记行程节点（地图轨迹）
 */
@Data
@TableName("travel_note_route")
public class TravelNoteRoute {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long noteId;

    /**
     * 第几天
     */
    private Integer dayIndex;

    private String poiName;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private LocalDateTime arriveTime;

    private String remark;

    private Integer sort;
}
