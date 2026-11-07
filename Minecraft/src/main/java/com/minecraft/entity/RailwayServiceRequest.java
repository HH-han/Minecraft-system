package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 站车服务预约
 */
@Data
@TableName("railway_service_request")
public class RailwayServiceRequest {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String requestNo;

    private Long userId;

    private Long serviceId;

    private String serviceName;

    private String stationName;

    private LocalDate serviceDate;

    private Integer passengerCount;

    private String contactName;

    private String contactPhone;

    private String remark;

    /** PENDING / ACCEPTED / COMPLETED / CANCELLED */
    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
