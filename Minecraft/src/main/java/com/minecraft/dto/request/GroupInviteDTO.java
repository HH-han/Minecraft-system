package com.minecraft.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class GroupInviteDTO {

    private Long groupId;

    /** 被邀请用户ID列表 */
    @NotEmpty(message = "被邀请用户不能为空")
    @Size(max = 200, message = "单次邀请人数不能超过200人")
    private List<Long> inviteeIds;
}
