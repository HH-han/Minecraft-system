package com.minecraft.recommendation.dto;

import lombok.Data;

/**
 * 推荐参数单项更新请求（批量接口的元素）。
 */
@Data
public class ConfigUpdateParam {

    /** GLOBAL / CATEGORY */
    private String scope;

    /** 分类编码；GLOBAL 时可为空串 */
    private String category;

    /** 参数键（须在 RecommendationDefaults 注册表内） */
    private String paramKey;

    /** 参数值（按 value_type 校验） */
    private String paramValue;
}
