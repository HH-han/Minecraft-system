package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.GroupComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupCommentMapper extends BaseMapper<GroupComment> {
    
    List<GroupComment> selectByPostId(@Param("postId") Long postId);
    
    List<GroupComment> selectByParentId(@Param("parentId") Long parentId);
}