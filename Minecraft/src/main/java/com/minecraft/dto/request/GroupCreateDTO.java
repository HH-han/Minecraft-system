package com.minecraft.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GroupCreateDTO {
    private String name;
    private String coverUrl;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private Integer maxMembers;
    private Boolean isPublic;
}