package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.GroupNotification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupNotificationMapper extends BaseMapper<GroupNotification> {
    
    List<GroupNotification> selectByReceiverId(@Param("receiverId") Long receiverId);
    
    int countUnread(@Param("receiverId") Long receiverId);
    
    int markAllRead(@Param("receiverId") Long receiverId);
}