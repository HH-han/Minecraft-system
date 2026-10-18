package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户会员信息
 */
@Data
@TableName("user_member")
public class UserMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** 当前等级ID */
    private Long levelId;

    /** 成长值 */
    private Integer growth;

    /** 是否付费会员 0 否 1 是 */
    private Integer isPaid;

    /** 付费会员到期时间 */
    private LocalDateTime expireTime;

    /** 生日（用于礼包） */
    private LocalDate birthday;

    /** 最后签到日期 */
    private LocalDate lastSignDate;

    /** 连续签到天数 */
    private Integer signContinuous;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
