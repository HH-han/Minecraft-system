package com.minecraft.entity.recommendation;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 景点推荐结果（attractions_recommendations -> attraction.id）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("attractions_recommendations")
public class AttractionRecommendation extends BaseRecommendationItem {
}
