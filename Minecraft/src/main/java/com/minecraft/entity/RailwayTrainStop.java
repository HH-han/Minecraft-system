package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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

    private LocalTime arriveTime;

    private LocalTime departTime;

    private Integer stopOver;

    private Integer dayOffset;

    private Integer secondClassPriceCents;

    private Integer firstClassPriceCents;

    private Integer sleeperPriceCents;
}
