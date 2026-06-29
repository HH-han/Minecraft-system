package com.minecraft.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class GroupInviteDTO {
    private Long groupId;
    private List<Long> inviteeIds;
}