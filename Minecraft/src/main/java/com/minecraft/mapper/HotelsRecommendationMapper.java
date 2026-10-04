package com.minecraft.mapper;

import com.minecraft.entity.recommendation.HotelRecommendation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HotelsRecommendationMapper extends RecommendationItemMapper<HotelRecommendation> {
}
