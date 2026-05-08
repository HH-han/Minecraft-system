package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minecraft.entity.ChatGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatGroupMapper extends BaseMapper<ChatGroup> {
    
    @Select("SELECT * FROM chat_group WHERE creator_id = #{creatorId}")
    List<ChatGroup> selectByCreatorId(@Param("creatorId") Long creatorId);
    
    @Select("SELECT * FROM chat_group WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<ChatGroup> searchByName(@Param("name") String name);
}