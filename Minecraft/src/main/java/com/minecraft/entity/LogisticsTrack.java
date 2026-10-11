package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 物流轨迹（每次回传一条）
 */
@Data
@TableName("logistics_track")
public class LogisticsTrack {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long shippingId;

    private String trackingNo;

    private String carrierCode;

    private LocalDateTime trackTime;

    /** 承运商原始状态码 */
    private String status;

    /** 轨迹描述 */
    private String context;

    private String location;

    private BigDecimal longitude;

    private BigDecimal latitude;

    /** 1-最新 */
    private Integer isLatest;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
