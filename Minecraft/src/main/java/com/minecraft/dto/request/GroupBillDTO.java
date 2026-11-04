package com.minecraft.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GroupBillDTO {

    private Long groupId;

    /** 账单金额 */
    @NotNull(message = "账单金额不能为空")
    @DecimalMin(value = "0.01", message = "账单金额必须大于0")
    private BigDecimal amount;

    /** 分类 */
    @Size(max = 50, message = "账单分类不能超过50个字符")
    private String category;

    /** 备注 */
    @Size(max = 200, message = "账单备注不能超过200个字符")
    private String description;

    /** 账单日期 */
    private LocalDate billDate;
}
