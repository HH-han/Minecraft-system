package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.Announcement;
import com.minecraft.entity.AnnouncementRead;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AnnouncementReadMapper extends BaseMapper<AnnouncementRead> {

    /** 幂等插入已读记录（唯一键冲突时忽略），返回影响行数 */
    @Insert("INSERT IGNORE INTO sys_announcement_read (announcement_id, user_id) VALUES (#{announcementId}, #{userId})")
    int insertIgnore(@Param("announcementId") Long announcementId, @Param("userId") Long userId);

    /** 用户未读公告列表（未过期的已发布公告中，未读的部分） */
    @Select("SELECT a.* FROM sys_announcement a " +
            "WHERE a.status = 1 AND a.deleted = 0 " +
            "  AND (a.expire_time IS NULL OR a.expire_time > NOW()) " +
            "  AND a.target_audience != 4 " +
            "  AND NOT EXISTS (SELECT 1 FROM sys_announcement_read r " +
            "                  WHERE r.announcement_id = a.id AND r.user_id = #{userId}) " +
            "ORDER BY a.is_top DESC, a.sort_weight DESC, a.publish_time DESC " +
            "LIMIT 20")
    List<Announcement> selectUnreadByUser(@Param("userId") Long userId);

    /** 用户未读数 */
    @Select("SELECT COUNT(*) FROM sys_announcement a " +
            "WHERE a.status = 1 AND a.deleted = 0 " +
            "  AND (a.expire_time IS NULL OR a.expire_time > NOW()) " +
            "  AND a.target_audience != 4 " +
            "  AND NOT EXISTS (SELECT 1 FROM sys_announcement_read r " +
            "                  WHERE r.announcement_id = a.id AND r.user_id = #{userId})")
    long countUnreadByUser(@Param("userId") Long userId);
}
