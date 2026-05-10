package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.GroupChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupChatMessageMapper extends BaseMapper<GroupChatMessage> {
    
    List<GroupChatMessage> selectByGroupId(@Param("groupId") Long groupId, @Param("limit") Integer limit);
}
