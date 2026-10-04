package com.minecraft.entity.recommendation;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 物品相似度（Item-CF 离线预计算 Top-K 邻居）。
 */
@Data
@TableName("recommendation_item_similarity")
public class RecommendationItemSimilarity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类编码 */
    private String category;

    private Long itemId;

    private Long neighborId;

    /** 余弦相似度（0-1） */
    private BigDecimal similarity;

    private LocalDateTime updatedAt;
}
