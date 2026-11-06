package com.minecraft.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 服务配置项 VO
 */
@Data
@Schema(description = "服务配置项")
public class ServiceItemVO {

    @Schema(description = "服务项ID")
    private Long id;

    @Schema(description = "唯一标识 key")
    private String itemKey;

    @Schema(description = "名称（中文）")
    private String name;

    @Schema(description = "名称（英文）")
    private String nameEn;

    @Schema(description = "图标名")
    private String icon;

    @Schema(description = "分组 finance/life/travel/shopping")
    private String category;

    @Schema(description = "跳转类型 none/route/url/wallet")
    private String linkType;

    @Schema(description = "跳转地址/钱包动作")
    private String link;

    @Schema(description = "组内排序")
    private Integer sort;
}
