package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.GroupAlbum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupAlbumMapper extends BaseMapper<GroupAlbum> {
    
    List<GroupAlbum> selectByGroupId(@Param("groupId") Long groupId);
    
    List<GroupAlbum> selectByGroupIdOrdered(@Param("groupId") Long groupId);
}