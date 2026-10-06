package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.Announcement;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface AnnouncementMapper extends BaseMapper<Announcement> {

    /** 已读数 +N（避免读-改-写竞态） */
    @Update("UPDATE sys_announcement SET read_count = read_count + #{count} WHERE id = #{id}")
    int incrReadCount(@Param("id") Long id, @Param("count") int count);

    /** 浏览量 +N（配合 Redis 缓冲批量落库） */
    @Update("UPDATE sys_announcement SET view_count = view_count + #{count} WHERE id = #{id}")
    int incrViewCount(@Param("id") Long id, @Param("count") int count);
}
