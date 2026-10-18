package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("railway_segment_inventory")
public class RailwaySegmentInventory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long serviceId;

    private Integer fromStopSeq;

    private Integer toStopSeq;

    /** SECOND_CLASS / FIRST_CLASS / SLEEPER */
    private String seatType;

    private Integer totalSeats;

    private Integer leftSeats;

    private Integer priceCents;
}
