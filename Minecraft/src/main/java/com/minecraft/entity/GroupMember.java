package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("group_member")
public class GroupMember {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long groupId;
    
    private Long userId;
    
    /**
     * 角色：owner-群主，admin-管理员，member-普通成员
     * 注意：聊天群组使用String类型(role="admin"/"member")
     * 旅行群组建议使用Integer类型(role=1/2/3)，但为兼容现有数据库结构，保留String
     */
    private String role;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime joinTime;
    
    private LocalDateTime exitTime;
    
    private String nickname;
}