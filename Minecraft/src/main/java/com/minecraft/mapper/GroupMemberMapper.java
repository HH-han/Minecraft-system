package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.GroupMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GroupMemberMapper extends BaseMapper<GroupMember> {
    
    @Select("SELECT * FROM group_member WHERE group_id = #{groupId}")
    List<GroupMember> selectByGroupId(@Param("groupId") Long groupId);
    
    @Select("SELECT * FROM group_member WHERE user_id = #{userId}")
    List<GroupMember> selectByUserId(@Param("userId") Long userId);
    
    @Select("SELECT COUNT(*) FROM group_member WHERE group_id = #{groupId} AND user_id = #{userId}")
    int countByGroupAndUser(@Param("groupId") Long groupId, @Param("userId") Long userId);
}