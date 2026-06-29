package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.GroupLocationHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupLocationHistoryMapper extends BaseMapper<GroupLocationHistory> {
    
    List<GroupLocationHistory> selectByGroupId(@Param("groupId") Long groupId);
    
    List<GroupLocationHistory> selectByGroupAndUser(@Param("groupId") Long groupId, @Param("userId") Long userId);
}