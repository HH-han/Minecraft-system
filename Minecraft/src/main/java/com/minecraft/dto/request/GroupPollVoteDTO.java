package com.minecraft.dto.request;

import lombok.Data;

@Data
public class GroupPollVoteDTO {
    private Long pollId;
    private Integer optionId;
    private Integer score;
}