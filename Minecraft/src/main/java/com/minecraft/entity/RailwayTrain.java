package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("railway_train")
public class RailwayTrain {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String trainNo;

    private String trainType;

    private Long startStationId;

    private Long endStationId;

    /** 二等座定员 */
    private Integer secondClassSeats;

    /** 一等座定员 */
    private Integer firstClassSeats;

    /** 卧铺定员 */
    private Integer sleeperSeats;

    /** 1正常 0停运 */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
