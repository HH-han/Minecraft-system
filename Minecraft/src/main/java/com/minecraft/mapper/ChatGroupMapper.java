package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.ChatGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatGroupMapper extends BaseMapper<ChatGroup> {
    
    List<ChatGroup> selectByCreatorId(@Param("creatorId") Long creatorId);
    
    List<ChatGroup> searchByName(@Param("name") String name);
    
    List<ChatGroup> selectByUserId(@Param("userId") Long userId);
}
