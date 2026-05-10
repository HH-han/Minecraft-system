package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.UserEmoji;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserEmojiMapper extends BaseMapper<UserEmoji> {
    List<UserEmoji> selectByUserId(@Param("userId") Long userId);
    Integer countByUserId(@Param("userId") Long userId);
}