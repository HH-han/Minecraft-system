package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class GroupPollVO {
    private Long id;
    private Long groupId;
    private String title;
    private List<String> options;
    private Integer status;
    private LocalDateTime createdAt;
    private Map<Integer, Integer> votes;
    private Integer votedOption;
    private Integer winningOption;
}