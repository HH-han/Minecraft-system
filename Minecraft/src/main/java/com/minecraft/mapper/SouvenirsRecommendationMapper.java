package com.minecraft.mapper;

import com.minecraft.entity.recommendation.SouvenirRecommendation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SouvenirsRecommendationMapper extends RecommendationItemMapper<SouvenirRecommendation> {
}
