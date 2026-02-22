package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("review_likes")
public class ReviewLikes {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer reviewId;
    
    private Long userId;
    
    private LocalDateTime createdAt;
}
