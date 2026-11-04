package com.minecraft.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateGroupRequest {

    /** 群名称 */
    @NotBlank(message = "群名称不能为空")
    @Size(max = 50, message = "群名称不能超过50个字符")
    private String name;

    /** 群简介 */
    @Size(max = 500, message = "群简介不能超过500个字符")
    private String description;

    @Size(max = 500, message = "群头像地址不能超过500个字符")
    private String avatar;

    private Long creatorId;

    /** 初始成员 */
    @Size(max = 500, message = "初始成员数量不能超过500个")
    private List<Long> memberIds;
}
