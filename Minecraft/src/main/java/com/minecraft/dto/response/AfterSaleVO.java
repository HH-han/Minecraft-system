package com.minecraft.dto.response;

import com.minecraft.entity.AfterSaleMessage;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 售后单展示对象
 */
@Data
public class AfterSaleVO {

    private Long id;

    private String afterSaleNo;

    private Long orderId;

    private String orderNo;

    /** 申请用户ID */
    private Long userId;

    /** 售后商品类型 */
    private String itemType;

    /** 售后商品ID */
    private Long itemId;

    private String itemName;

    private String itemImage;

    private Integer quantity;

    private Integer type;

    private String typeDesc;

    private Integer reasonType;

    private String reason;

    /** 凭证图片JSON */
    private String evidenceUrls;

    private BigDecimal refundAmount;

    private BigDecimal actualAmount;

    private Integer status;

    private String statusDesc;

    private String auditRemark;

    /** 审核人ID */
    private Long auditBy;

    /** 审核时间 */
    private LocalDateTime auditTime;

    private String returnCarrier;

    private String returnTracking;

    /** 退回时间 */
    private LocalDateTime returnTime;

    /** 商家收货时间 */
    private LocalDateTime receiveTime;

    /** 关闭原因 */
    private String closeReason;

    private String refundNo;

    private LocalDateTime refundTime;

    private LocalDateTime createTime;

    /** 协商记录（详情接口返回） */
    private List<AfterSaleMessage> messages;
}
