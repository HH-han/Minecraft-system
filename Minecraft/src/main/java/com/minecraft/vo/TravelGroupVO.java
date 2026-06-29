package com.minecraft.vo;

import com.minecraft.entity.GroupMember;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TravelGroupVO {
    private Long id;
    private String name;
    private String coverUrl;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private Integer maxMembers;
    private Integer currentMembers;
    private Boolean isPublic;
    private Integer status;
    private Long createdBy;
    private String creatorName;
    private String creatorAvatar;
    private Integer userRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GroupMember> members;
}