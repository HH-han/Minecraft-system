package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.recommendation.RecommendationJobLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecommendationJobLogMapper extends BaseMapper<RecommendationJobLog> {
}
