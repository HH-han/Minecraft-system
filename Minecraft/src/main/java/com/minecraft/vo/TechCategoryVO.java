package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 技术分类 VO（含技术项列表），对应 SQL 中 tech_categories LEFT JOIN tech_items 查询。
 */
@Data
public class TechCategoryVO {
    private Integer id;
    private String categoryName;
    private String themeClass;
    private Integer sortOrder;
    private LocalDateTime createdAt;

    /**
     * 该分类下的技术项列表
     */
    private List<TechItem> items;

    @Data
    public static class TechItem {
        private Integer id;
        private Integer categoryId;
        private String techName;
        private String description;
        private Integer sortOrder;
    }
}
