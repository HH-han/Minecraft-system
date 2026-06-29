package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("group_bill")
public class GroupBill {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long groupId;
    
    private Long payerId;
    
    private BigDecimal amount;
    
    private String category;
    
    private String description;
    
    private LocalDate billDate;
    
    private Boolean isSettled;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}