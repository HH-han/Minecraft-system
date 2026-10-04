package com.minecraft.entity.recommendation;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 推荐算法参数配置（GLOBAL 全局 / CATEGORY 分类两级）。
 */
@Data
@TableName("recommendation_config")
public class RecommendationConfig {

    public static final String SCOPE_GLOBAL = "GLOBAL";
    public static final String SCOPE_CATEGORY = "CATEGORY";
    public static final String GLOBAL_CATEGORY = "";

    public static final String TYPE_NUMBER = "NUMBER";
    public static final String TYPE_BOOLEAN = "BOOLEAN";
    public static final String TYPE_STRING = "STRING";

    @TableId(type = IdType.AUTO)
    private Long id;

    private String scope;

    /** 分类编码，GLOBAL 时为空串 */
    private String category;

    private String paramKey;

    private String paramValue;

    /** NUMBER / BOOLEAN / STRING */
    private String valueType;

    private String description;

    private Long updatedBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
