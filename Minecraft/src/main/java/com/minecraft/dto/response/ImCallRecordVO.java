package com.minecraft.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通话记录（管理端）
 */
@Data
public class ImCallRecordVO {
    private Long id;
    private String callId;
    private Long callerId;
    private String callerName;
    private Long receiverId;
    private String receiverName;
    /** voice/video */
    private String callType;
    private String status;
    /** 通话时长(秒) */
    private Integer duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
}
