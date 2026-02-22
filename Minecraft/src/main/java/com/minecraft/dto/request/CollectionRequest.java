package com.minecraft.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class CollectionRequest {
    @NotNull(message = "收藏类型不能为空")
    private String itemType;

    @NotNull(message = "收藏对象ID不能为空")
    private Long itemId;
}
