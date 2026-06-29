package com.minecraft.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GroupUpdateDTO {
    private Long id;
    private String name;
    private String coverUrl;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private Integer maxMembers;
    private Boolean isPublic;
}