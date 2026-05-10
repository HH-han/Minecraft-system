package com.minecraft.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateGroupRequest {
    private String name;
    private String description;
    private String avatar;
    private Long creatorId;
    private List<Long> memberIds;
}
