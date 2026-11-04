package com.minecraft.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SingleChatMessage {

    private Long senderId;

    private Long receiverId;

    @NotBlank(message = "消息内容不能为空")
    @Size(max = 2000, message = "消息内容不能超过2000个字符")
    private String content;

    @Size(max = 20, message = "消息类型不能超过20个字符")
    private String messageType = "text";
}
