package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("group_invite")
public class GroupInvite {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long groupId;
    
    private Long inviterId;
    
    private Long inviteeId;
    
    private Integer type;
    
    private Integer status;
    
    private LocalDateTime expireAt;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    private LocalDateTime handledAt;
}