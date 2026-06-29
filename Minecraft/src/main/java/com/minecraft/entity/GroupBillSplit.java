package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("group_bill_split")
public class GroupBillSplit {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long billId;
    
    private Long userId;
    
    private BigDecimal shareAmount;
    
    private Boolean isPaid;
}