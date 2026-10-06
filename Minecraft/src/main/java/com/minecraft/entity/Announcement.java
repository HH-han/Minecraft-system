package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_announcement")
public class Announcement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String summary;
    private String content;
    private Integer type;
    private Integer level;
    private Long categoryId;
    private String coverImage;
    private Integer displayMode;
    private Integer targetAudience;
    private Integer status;
    private Integer isTop;
    private Integer sortWeight;
    private LocalDateTime publishTime;
    private LocalDateTime expireTime;
    private Long viewCount;
    private Long readCount;
    private Long creatorId;
    private String creatorName;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
