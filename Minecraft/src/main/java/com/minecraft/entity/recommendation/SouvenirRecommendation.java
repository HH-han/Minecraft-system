package com.minecraft.entity.recommendation;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 纪念品（特产）推荐结果（souvenirs_recommendations -> product.id）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("souvenirs_recommendations")
public class SouvenirRecommendation extends BaseRecommendationItem {
}
