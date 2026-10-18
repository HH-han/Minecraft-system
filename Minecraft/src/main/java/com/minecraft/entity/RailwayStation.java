package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("railway_station")
public class RailwayStation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String stationCode;

    private String stationName;

    private String cityName;

    private String pinyin;

    private String initial;
}
