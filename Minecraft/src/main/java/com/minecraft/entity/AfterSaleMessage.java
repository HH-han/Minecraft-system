package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 售后协商记录
 */
@Data
@TableName("after_sale_message")
public class AfterSaleMessage {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long afterSaleId;

    /** 1-用户 2-商家 3-系统 */
    private Integer senderType;

    private Long senderId;

    private String content;

    /** 附件JSON */
    private String evidenceUrls;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
