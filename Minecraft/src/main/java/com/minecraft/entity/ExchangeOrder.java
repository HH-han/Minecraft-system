package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("exchange_order")
public class ExchangeOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private Long productId;
    /** 关联的物流订单ID（orders.id），兑换成功后生成 */
    private Long orderId;
    private String productName;
    private Integer pointsUsed;
    private Integer quantity;
    private String status;
    private String address;
    private String phone;
    private String receiver;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // ===== 关联物流订单的展示字段（非表字段） =====
    /** 物流状态：0-待发货 1-部分发货 2-已发货 3-已签收 */
    @TableField(exist = false)
    private Integer shippingStatus;

    /** 物流状态描述 */
    @TableField(exist = false)
    private String statusDesc;

    /** 承运商编码 */
    @TableField(exist = false)
    private String carrierCode;

    /** 运单号 */
    @TableField(exist = false)
    private String trackingNo;
}