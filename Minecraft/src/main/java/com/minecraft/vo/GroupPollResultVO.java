package com.minecraft.vo;

import lombok.Data;

import java.util.Map;

@Data
public class GroupPollResultVO {
    private Long pollId;
    private Integer totalVotes;
    private Map<Integer, Integer> optionVotes;
    private Map<Integer, Double> optionPercentages;
}