package com.minecraft.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class LikeRequest {
    @NotNull(message = "点赞类型不能为空")
    private String itemType;

    @NotNull(message = "点赞对象ID不能为空")
    private Long itemId;
}
