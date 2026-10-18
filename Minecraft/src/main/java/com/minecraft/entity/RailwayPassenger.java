package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("railway_passenger")
public class RailwayPassenger {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String name;

    private String idCardNo;

    /** ADULT / CHILD / STUDENT */
    private String passengerType;

    private String phone;

    /** 1已核验 0待核验 */
    private Integer verifyStatus;

    private LocalDateTime createTime;
}
