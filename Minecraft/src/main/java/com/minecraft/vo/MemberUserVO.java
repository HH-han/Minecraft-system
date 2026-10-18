package com.minecraft.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 管理端会员列表项
 */
@Data
public class MemberUserVO {

    private Long userId;

    private String account;

    private String username;

    private String nickname;

    private String avatar;

    private Integer growth;

    /** 等级序号 */
    private Integer level;

    private String levelName;

    private BigDecimal discount;

    private BigDecimal pointsRate;

    private Integer isPaid;

    private LocalDateTime expireTime;

    private LocalDate lastSignDate;

    private Integer signContinuous;

    private LocalDateTime createTime;
}
