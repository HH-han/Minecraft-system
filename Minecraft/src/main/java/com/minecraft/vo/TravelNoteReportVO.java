package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 旅游笔记举报记录 VO（管理端）
 */
@Data
public class TravelNoteReportVO {
    private Long id;
    private Long noteId;
    private String noteTitle;
    private Long reporterId;
    private String reporterName;
    private String reason;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime handleTime;
}
