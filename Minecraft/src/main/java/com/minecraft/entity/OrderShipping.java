package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 发货单（一单可拆多包裹，商品以快照字段冗余）
 */
@Data
@TableName("order_shipping")
public class OrderShipping {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String shippingNo;

    private Long orderId;

    private String orderNo;

    private Long userId;

    // ===== 商品快照（来自 orders 单商品模型）=====
    private String itemType;

    private Long itemId;

    private String itemName;

    private String itemImage;

    /** 本包裹数量 */
    private Integer quantity;

    // ===== 承运与收货信息 =====
    private Long carrierId;

    private String carrierCode;

    private String trackingNo;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private BigDecimal packageWeight;

    private BigDecimal freight;

    /** ShippingStatus: 0-待发货 1-已发货 2-运输中 3-派送中 4-已签收 5-拒收 6-退回 7-异常 */
    private Integer status;

    private LocalDateTime shipTime;

    private LocalDateTime receiveTime;

    private LocalDateTime expectTime;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
