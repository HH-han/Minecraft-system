package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.recommendation.RecommendationConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecommendationConfigMapper extends BaseMapper<RecommendationConfig> {

    /**
     * 按唯一键 (scope, category, param_key) 查询单行。
     */
    RecommendationConfig selectByKey(@Param("scope") String scope,
                                      @Param("category") String category,
                                      @Param("paramKey") String paramKey);
}
