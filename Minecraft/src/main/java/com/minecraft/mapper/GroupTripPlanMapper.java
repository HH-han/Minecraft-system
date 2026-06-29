package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.GroupTripPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupTripPlanMapper extends BaseMapper<GroupTripPlan> {
    
    List<GroupTripPlan> selectByGroupId(@Param("groupId") Long groupId);
    
    List<GroupTripPlan> selectByGroupIdOrdered(@Param("groupId") Long groupId);
}