package com.minecraft.entity.recommendation;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 酒店推荐结果（hotels_recommendations -> hotel.id）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hotels_recommendations")
public class HotelRecommendation extends BaseRecommendationItem {
}
