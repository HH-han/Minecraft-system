package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理端成长值流水列表项
 */
@Data
public class MemberGrowthRecordVO {

    private Long id;

    private Long userId;

    private String account;

    private String nickname;

    private Integer changeValue;

    private Integer afterValue;

    private Integer source;

    private String bizId;

    private String remark;

    private LocalDateTime createTime;
}
