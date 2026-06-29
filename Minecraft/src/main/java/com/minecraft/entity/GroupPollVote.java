package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("group_poll_vote")
public class GroupPollVote {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long pollId;
    
    private Long userId;
    
    private Integer optionId;
    
    private Integer score;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime votedAt;
}