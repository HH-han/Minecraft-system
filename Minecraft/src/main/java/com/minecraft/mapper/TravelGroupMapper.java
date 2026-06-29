package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.TravelGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TravelGroupMapper extends BaseMapper<TravelGroup> {
    
    List<TravelGroup> selectByUserId(@Param("userId") Long userId);
    
    List<TravelGroup> searchPublicGroups(@Param("keyword") String keyword);
}