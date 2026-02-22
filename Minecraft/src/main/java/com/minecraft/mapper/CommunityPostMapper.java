package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.CommunityPost;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommunityPostMapper extends BaseMapper<CommunityPost> {
}
