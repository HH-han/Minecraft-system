package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class GroupPollVO {
    private Long id;
    private Long groupId;
    private Long creatorId;
    private String title;
    private String description;
    /** 投票类型 */
    private Integer type;
    private List<String> options;
    private LocalDateTime endTime;
    /** 是否匿名投票 */
    private Boolean isAnonymous;
    private Integer status;
    private LocalDateTime createdAt;
    private Map<Integer, Integer> votes;
    private Integer votedOption;
    private Integer winningOption;
}