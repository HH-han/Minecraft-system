package com.minecraft.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 管理端发放会员权益请求
 */
@Data
public class MemberBenefitGrantDTO {

    @NotBlank(message = "权益类型不能为空")
    private String benefitType;

    private String content;
}
