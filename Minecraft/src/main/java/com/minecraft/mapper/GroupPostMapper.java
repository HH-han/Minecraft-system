package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.GroupPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupPostMapper extends BaseMapper<GroupPost> {
    
    List<GroupPost> selectByGroupId(@Param("groupId") Long groupId);
    
    int incrementLikeCount(@Param("postId") Long postId);
    
    int decrementLikeCount(@Param("postId") Long postId);
    
    int incrementCommentCount(@Param("postId") Long postId);
}