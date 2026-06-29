package com.minecraft.dto.request;

import lombok.Data;

@Data
public class GroupPostDTO {
    private Long groupId;
    private String content;
    private String images;
    private String location;
}