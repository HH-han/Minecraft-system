package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 旅游笔记数据看板 VO（管理端）
 */
@Data
public class TravelNoteStatVO {
    private Long totalNotes;
    private Long publishedCount;
    private Long pendingCount;
    private Long totalViews;
    private Long totalLikes;
    private Long totalCollects;
    private Long totalComments;
    private Long totalReports;
    private List<HotDestination> hotDestinations;
    private List<TopAuthor> topAuthors;

    @Data
    public static class HotDestination {
        private String destination;
        private Long noteCount;
    }

    @Data
    public static class TopAuthor {
        private Long userId;
        private String username;
        private String avatar;
        private Long noteCount;
        private Long totalLikes;
    }
}
