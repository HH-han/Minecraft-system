package com.minecraft.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 发货单展示对象
 */
@Data
public class OrderShippingVO {

    private Long id;

    private String shippingNo;

    private Long orderId;

    private String orderNo;

    /** 下单用户ID */
    private Long userId;

    /** 商品类型 */
    private String itemType;

    /** 商品ID */
    private Long itemId;

    private String itemName;

    private String itemImage;

    private Integer quantity;

    private Long carrierId;

    private String carrierCode;

    private String carrierName;

    private String trackingNo;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    /** 包裹重量(kg) */
    private BigDecimal packageWeight;

    private BigDecimal freight;

    /** 备注 */
    private String remark;

    private Integer status;

    private String statusDesc;

    private LocalDateTime shipTime;

    private LocalDateTime receiveTime;

    private LocalDateTime expectTime;

    /** 轨迹列表（详情接口返回） */
    private List<LogisticsTrackDTO> tracks;
}
