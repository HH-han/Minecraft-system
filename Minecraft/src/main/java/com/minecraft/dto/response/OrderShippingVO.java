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

    private String itemName;

    private String itemImage;

    private Integer quantity;

    private String carrierCode;

    private String carrierName;

    private String trackingNo;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private BigDecimal freight;

    private Integer status;

    private String statusDesc;

    private LocalDateTime shipTime;

    private LocalDateTime receiveTime;

    private LocalDateTime expectTime;

    /** 轨迹列表（详情接口返回） */
    private List<LogisticsTrackDTO> tracks;
}
