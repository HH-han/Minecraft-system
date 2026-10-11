package com.minecraft.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 朋友圈动态视图对象
 */
@Data
public class MomentVO {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String content;
    private List<String> images;
    private String location;
    /** 可见范围 0-公开 1-仅好友 2-私密 */
    private Integer visibility;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createTime;
    /** 当前用户是否已点赞 */
    private Boolean liked;
    /** 点赞用户列表 */
    private List<MomentUserVO> likes;
    /** 评论列表 */
    private List<MomentCommentVO> comments;
}
