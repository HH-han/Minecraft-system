package com.minecraft.mapper;

import com.minecraft.entity.recommendation.RestaurantRecommendation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RestaurantsRecommendationMapper extends RecommendationItemMapper<RestaurantRecommendation> {
}
