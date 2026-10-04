package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.recommendation.RecommendationRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 推荐人工干预规则 Mapper。
 */
@Mapper
public interface RecommendationRuleMapper extends BaseMapper<RecommendationRule> {
}
