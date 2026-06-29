package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.GroupInvite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupInviteMapper extends BaseMapper<GroupInvite> {
    
    List<GroupInvite> selectByInviteeId(@Param("inviteeId") Long inviteeId);
    
    List<GroupInvite> selectByGroupId(@Param("groupId") Long groupId);
    
    GroupInvite selectByGroupAndUser(@Param("groupId") Long groupId, @Param("userId") Long userId);
}