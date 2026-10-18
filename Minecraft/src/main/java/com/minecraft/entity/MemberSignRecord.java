package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 签到记录
 */
@Data
@TableName("member_sign_record")
public class MemberSignRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private LocalDate signDate;

    /** 当日连续签到天数 */
    private Integer continuous;

    private Integer giveGrowth;

    private Integer givePoints;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
