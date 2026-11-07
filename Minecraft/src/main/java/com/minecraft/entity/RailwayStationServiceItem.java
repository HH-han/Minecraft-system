package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 站车服务项目
 */
@Data
@TableName("railway_station_service")
public class RailwayStationServiceItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String serviceCode;

    private String name;

    /** STATION / TRAIN / CARE */
    private String category;

    private String description;

    private String icon;

    private String priceDesc;

    private Integer needBooking;

    private Integer sort;

    private Integer status;

    private LocalDateTime createTime;
}
