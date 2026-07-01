package com.minecraft.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GroupPollDTO {
    private Long groupId;
    private String title;
    private String description;
    private Integer type;
    private List<String> options;
    private LocalDateTime endTime;
    private Boolean isAnonymous;
}