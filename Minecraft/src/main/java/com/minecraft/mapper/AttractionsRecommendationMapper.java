package com.minecraft.mapper;

import com.minecraft.entity.recommendation.AttractionRecommendation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttractionsRecommendationMapper extends RecommendationItemMapper<AttractionRecommendation> {
}
