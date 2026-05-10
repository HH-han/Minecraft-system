package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    int addPoints(@Param("userId") Long userId, @Param("points") Integer points);
    
    int deductPoints(@Param("userId") Long userId, @Param("points") Integer points);
}
