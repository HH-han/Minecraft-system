package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.Tags;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TagsMapper extends BaseMapper<Tags> {
}
