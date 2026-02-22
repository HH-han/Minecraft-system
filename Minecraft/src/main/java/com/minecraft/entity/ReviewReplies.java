package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("review_replies")
public class ReviewReplies {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer reviewId;
    
    private Long userId;
    
    private String content;
    
    private Integer likesCount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
