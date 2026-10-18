package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 成长值流水
 */
@Data
@TableName("member_growth_record")
public class MemberGrowthRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** 变化值（正负） */
    private Integer changeValue;

    /** 变化后成长值 */
    private Integer afterValue;

    /** 1 消费 2 签到 3 评价 4 分享 5 开通会员 6 管理员调整 */
    private Integer source;

    /** 业务ID（订单号等） */
    private String bizId;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
