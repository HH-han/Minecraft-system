package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.TravelNote;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface TravelNoteMapper extends BaseMapper<TravelNote> {

    @Select("SELECT IFNULL(SUM(view_count), 0) AS totalViews, " +
            "IFNULL(SUM(like_count), 0) AS totalLikes, " +
            "IFNULL(SUM(collect_count), 0) AS totalCollects, " +
            "IFNULL(SUM(comment_count), 0) AS totalComments " +
            "FROM travel_note WHERE deleted = 0")
    Map<String, Object> selectStatSummary();

    @Select("SELECT destination, COUNT(*) AS noteCount FROM travel_note " +
            "WHERE deleted = 0 AND status = 2 AND destination IS NOT NULL AND destination != '' " +
            "GROUP BY destination ORDER BY noteCount DESC LIMIT 5")
    java.util.List<Map<String, Object>> selectHotDestinations();

    @Select("SELECT user_id AS userId, COUNT(*) AS noteCount, " +
            "IFNULL(SUM(like_count), 0) AS totalLikes " +
            "FROM travel_note WHERE deleted = 0 AND status = 2 " +
            "GROUP BY user_id ORDER BY noteCount DESC LIMIT 5")
    java.util.List<Map<String, Object>> selectTopAuthors();
}
