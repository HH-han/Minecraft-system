package com.minecraft.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GroupPollDTO {

    private Long groupId;

    @NotBlank(message = "投票标题不能为空")
    @Size(max = 100, message = "投票标题不能超过100个字符")
    private String title;

    @Size(max = 500, message = "投票描述不能超过500个字符")
    private String description;

    /** 投票类型 */
    private Integer type;

    /** 投票选项，至少2个 */
    @NotEmpty(message = "投票选项不能为空")
    @Size(min = 2, max = 20, message = "投票选项数量需在2到20个之间")
    private List<String> options;

    /** 截止时间 */
    private LocalDateTime endTime;

    /** 是否匿名投票 */
    private Boolean isAnonymous;
}
