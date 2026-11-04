package com.minecraft.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 运费模板新增/编辑请求
 */
@Data
public class FreightSaveRequest {

    /** 主键，为空表示新增 */
    private Long id;

    @NotBlank(message = "模板名称不能为空")
    @Size(max = 50, message = "模板名称不能超过50个字符")
    private String name;

    /** 1-按件 2-按重量 3-按体积 */
    @NotNull(message = "计费方式不能为空")
    @Min(value = 1, message = "计费方式不合法")
    @Max(value = 3, message = "计费方式不合法")
    private Integer chargeType;

    @DecimalMin(value = "0", message = "首件/首重不能为负数")
    private BigDecimal firstUnit;

    @DecimalMin(value = "0", message = "首费不能为负数")
    private BigDecimal firstPrice;

    @DecimalMin(value = "0", message = "续件/续重不能为负数")
    private BigDecimal extraUnit;

    @DecimalMin(value = "0", message = "续费不能为负数")
    private BigDecimal extraPrice;

    @Size(max = 500, message = "包邮地区配置不能超过500个字符")
    private String freeRegions;

    @Size(max = 500, message = "排除地区配置不能超过500个字符")
    private String excludeRegions;

    /** 状态：0-禁用 1-启用 */
    @Min(value = 0, message = "状态值不合法")
    @Max(value = 1, message = "状态值不合法")
    private Integer status;
}
