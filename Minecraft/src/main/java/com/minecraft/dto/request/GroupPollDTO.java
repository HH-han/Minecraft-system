package com.minecraft.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupPollDTO {
    private Long groupId;
    private String title;
    private String description;
    private Integer type;
    private String options;
    private LocalDateTime endTime;
    private Boolean isAnonymous;
}