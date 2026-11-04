package com.minecraft.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GroupPollVoteDTO {

    @NotNull(message = "投票ID不能为空")
    private Long pollId;

    @NotNull(message = "投票选项不能为空")
    private Integer optionId;

    /** 评分（评分型投票使用） */
    @Min(value = 0, message = "评分不能为负数")
    private Integer score;
}
