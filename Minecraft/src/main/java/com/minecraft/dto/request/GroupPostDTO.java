package com.minecraft.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GroupPostDTO {

    private Long groupId;

    @NotBlank(message = "动态内容不能为空")
    @Size(max = 2000, message = "动态内容不能超过2000个字符")
    private String content;

    /** 图片JSON/逗号分隔地址 */
    @Size(max = 2000, message = "图片数据不能超过2000个字符")
    private String images;

    @Size(max = 200, message = "位置信息不能超过200个字符")
    private String location;
}
