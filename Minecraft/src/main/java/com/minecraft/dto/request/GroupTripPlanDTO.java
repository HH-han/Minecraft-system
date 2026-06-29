package com.minecraft.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GroupTripPlanDTO {
    private Long groupId;
    private Integer dayIndex;
    private LocalDate date;
    private String timeSlot;
    private String activity;
    private String location;
    private String transport;
    private String notes;
}