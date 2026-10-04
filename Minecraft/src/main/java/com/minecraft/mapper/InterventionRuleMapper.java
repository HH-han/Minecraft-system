package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.recommendation.InterventionRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 业务级人工干预规则 Mapper。
 */
@Mapper
public interface InterventionRuleMapper extends BaseMapper<InterventionRule> {
}
