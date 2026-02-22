package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("months")
public class Month {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String monthName;
    
    private Integer monthOrder;
    
    private Integer isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
