package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.AITripRecommendation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AITripRecommendationMapper extends BaseMapper<AITripRecommendation> {
    
    List<AITripRecommendation> selectByUserId(@Param("userId") Long userId);
    
    List<AITripRecommendation> selectByGroupId(@Param("groupId") Long groupId);
}