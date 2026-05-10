package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.Emoji;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmojiMapper extends BaseMapper<Emoji> {
    List<Emoji> selectByCategory(@Param("category") String category);
    List<Emoji> selectAllActive();
    List<String> selectAllCategories();
}