package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Update("UPDATE user SET points = points + #{points} WHERE id = #{userId}")
    int addPoints(Long userId, Integer points);
    
    @Update("UPDATE user SET points = points - #{points} WHERE id = #{userId} AND points >= #{points}")
    int deductPoints(Long userId, Integer points);
}
