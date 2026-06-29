package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.GroupPoll;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupPollMapper extends BaseMapper<GroupPoll> {
    
    List<GroupPoll> selectByGroupId(@Param("groupId") Long groupId);
    
    List<GroupPoll> selectActiveByGroupId(@Param("groupId") Long groupId);
}