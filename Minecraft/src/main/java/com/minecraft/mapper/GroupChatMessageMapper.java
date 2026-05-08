package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.GroupChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GroupChatMessageMapper extends BaseMapper<GroupChatMessage> {
    
    @Select("SELECT * FROM group_chat_message WHERE group_id = #{groupId} ORDER BY create_time DESC LIMIT #{limit}")
    List<GroupChatMessage> selectByGroupId(@Param("groupId") Long groupId, @Param("limit") Integer limit);
}