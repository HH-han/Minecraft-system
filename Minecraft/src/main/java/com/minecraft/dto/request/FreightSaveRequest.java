package com.minecraft.dto.request;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 运费模板新增/编辑请求
 */
@Data
public class FreightSaveRequest {

    private Long id;

    private String name;

    /** 1-按件 2-按重量 3-按体积 */
    private Integer chargeType;

    private BigDecimal firstUnit;

    private BigDecimal firstPrice;

    private BigDecimal extraUnit;

    private BigDecimal extraPrice;

    private String freeRegions;

    private String excludeRegions;

    private Integer status;
}
