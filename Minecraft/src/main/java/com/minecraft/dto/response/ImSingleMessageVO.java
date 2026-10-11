package com.minecraft.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 单聊消息（管理端）
 */
@Data
public class ImSingleMessageVO {
    private Long id;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private Long receiverId;
    private String receiverName;
    private String receiverAvatar;
    private String content;
    /** text-文本 image-图片 video-视频 audio-音频 emoji-表情 */
    private String messageType;
    /** 0-未读 1-已读 */
    private Integer isRead;
    private LocalDateTime createTime;
}
