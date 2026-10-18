package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalTime;

@Data
@TableName("railway_stop_station")
public class RailwayStopStation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long serviceId;

    private Long stationId;

    private Integer stopSeq;

    private LocalTime arriveTime;

    private LocalTime departTime;

    private Integer stopOver;

    private Integer dayOffset;
}
