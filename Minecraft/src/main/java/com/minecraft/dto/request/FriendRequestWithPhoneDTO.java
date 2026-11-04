package com.minecraft.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FriendRequestWithPhoneDTO {

    private Long senderId;

    private Long receiverId;

    /** 接收者手机号，与 receiverId 二选一 */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String receiverPhone;

    @Size(max = 200, message = "好友申请留言不能超过200个字符")
    private String message;
}
