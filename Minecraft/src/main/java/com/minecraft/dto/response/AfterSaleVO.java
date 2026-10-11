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

    private String returnCarrier;

    private String returnTracking;

    private String refundNo;

    private LocalDateTime refundTime;

    private LocalDateTime createTime;

    /** 协商记录（详情接口返回） */
    private List<AfterSaleMessage> messages;
}
