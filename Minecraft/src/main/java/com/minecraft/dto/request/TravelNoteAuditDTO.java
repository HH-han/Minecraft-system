package com.minecraft.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 旅游笔记审核请求（管理端）
 */
@Data
public class TravelNoteAuditDTO {

    @NotNull(message = "审核结果不能为空")
    private Boolean approved;

    private String reason;
}
