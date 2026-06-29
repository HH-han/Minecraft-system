package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.GroupTravelDiary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GroupTravelDiaryMapper extends BaseMapper<GroupTravelDiary> {
    
    GroupTravelDiary selectByGroupId(@Param("groupId") Long groupId);
}