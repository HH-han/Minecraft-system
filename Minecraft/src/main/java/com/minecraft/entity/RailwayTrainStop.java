package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalTime;

@Data
@TableName("railway_train_stop")
public class RailwayTrainStop {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long trainId;

    private Long stationId;

    private Integer stopSeq;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime arriveTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime departTime;

    private Integer stopOver;

    private Integer dayOffset;

    private Integer secondClassPriceCents;

    private Integer firstClassPriceCents;

    private Integer sleeperPriceCents;
}
