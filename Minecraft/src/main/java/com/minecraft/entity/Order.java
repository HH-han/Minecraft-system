package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String orderNo;
    
    private String itemType;
    
    private Long itemId;
    
    private String itemName;

    private String image;

    /** 收件人（下单时留存，发货时自动带入） */
    private String receiverName;

    /** 收件电话 */
    private String receiverPhone;

    /** 收件地址 */
    private String receiverAddress;

    private BigDecimal amount;
    
    private Integer quantity;
    
    private String remark;
    
    private String status;

    /** 物流状态 0-待发货 1-部分发货 2-已发货 3-已签收 */
    private Integer shippingStatus;

    /** 售后状态 0-无 1-售后中 2-售后完成 */
    private Integer afterSaleStatus;

    private String carrierCode;

    private String trackingNo;

    private LocalDateTime shipTime;

    private LocalDateTime receiveTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
