package com.minecraft.entity.recommendation;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 餐厅（美食）推荐结果（restaurants_recommendations -> food.id）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("restaurants_recommendations")
public class RestaurantRecommendation extends BaseRecommendationItem {
}
