package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 售后单（商品以快照字段冗余）
 */
@Data
@TableName("after_sale")
public class AfterSale {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String afterSaleNo;

    private Long orderId;

    private String orderNo;

    private Long userId;

    // ===== 商品快照 =====
    private String itemType;

    private Long itemId;

    private String itemName;

    private String itemImage;

    private Integer quantity;

    // ===== 申请信息 =====
    /** AfterSaleType: 1-仅退款 2-退货退款 3-换货 */
    private Integer type;

    private Integer reasonType;

    private String reason;

    /** 凭证图片JSON */
    private String evidenceUrls;

    private BigDecimal refundAmount;

    private BigDecimal actualAmount;

    /** AfterSaleStatus: 0-待审核 1-审核通过 2-已驳回 3-待买家退货 4-待商家收货 5-退款中 6-已完成 7-已关闭 */
    private Integer status;

    private Long auditBy;

    private LocalDateTime auditTime;

    private String auditRemark;

    private String returnCarrier;

    private String returnTracking;

    private LocalDateTime returnTime;

    private LocalDateTime receiveTime;

    private LocalDateTime refundTime;

    private String refundNo;

    private String closeReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
