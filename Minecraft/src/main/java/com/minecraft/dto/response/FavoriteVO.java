package com.minecraft.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 收藏项视图对象
 */
@Data
public class FavoriteVO {
    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String content;
    private String cover;
    private String url;
    private String fileName;
    private Long fileSize;
    private List<String> tags;
    private String source;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
