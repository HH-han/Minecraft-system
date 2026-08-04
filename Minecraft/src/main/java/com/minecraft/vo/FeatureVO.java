package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 核心功能 VO（含子项列表），对应 SQL 中 features LEFT JOIN feature_items 查询。
 */
@Data
public class FeatureVO {
    private Integer id;
    private String icon;
    private String title;
    private String description;
    private String bgColor;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 该功能下的子项列表
     */
    private List<FeatureItem> items;

    @Data
    public static class FeatureItem {
        private Integer id;
        private Integer featureId;
        private String itemName;
        private Integer sortOrder;
    }
}
