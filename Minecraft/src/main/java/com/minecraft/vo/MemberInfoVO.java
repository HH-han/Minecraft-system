package com.minecraft.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户会员中心信息
 */
@Data
public class MemberInfoVO {

    private Long userId;

    private String nickname;

    private String avatar;

    /** 成长值 */
    private Integer growth;

    /** 等级序号 */
    private Integer level;

    private String levelName;

    private String levelIcon;

    /** 折扣率 */
    private BigDecimal discount;

    /** 积分倍率 */
    private BigDecimal pointsRate;

    /** 当前等级成长值下限 */
    private Integer levelMinGrowth;

    /** 距下一等级所需成长值，0 表示已满级 */
    private Integer nextLevelGrowth;

    /** 是否付费会员 */
    private Integer isPaid;

    private LocalDateTime expireTime;

    /** 今日是否已签到 */
    private Integer todaySigned;

    private Integer signContinuous;

    /** 当前等级权益编码列表 */
    private List<String> benefits;
}
