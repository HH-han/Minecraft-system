package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 物流承运商
 */
@Data
@TableName("logistics_carrier")
public class LogisticsCarrier {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 承运商编码 SF/YTO/ZTO... */
    private String code;

    private String name;

    private String logoUrl;

    /** 接口类型 kuaidi100/kuaidiniao/aliyun */
    private String apiType;

    private String apiKey;

    private String apiSecret;

    private String apiCustomer;

    private String phone;

    private String website;

    private Integer sort;

    /** 1-启用 0-禁用 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
