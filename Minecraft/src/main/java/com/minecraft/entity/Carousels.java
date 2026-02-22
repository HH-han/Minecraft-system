package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("carousels")
public class Carousels {
    private Integer id;
    private String title;
    private String subtitle;
    private String imageUrl;
    private String mobileImageUrl;
    private String thumbnailUrl;
    private String linkType;
    private String linkUrl;
    private String linkTarget;
    private Integer targetId;
    private String buttonText;
    private String buttonColor;
    private String textColor;
    private Boolean textShadow;
    private String overlayColor;
    private Double overlayOpacity;
    private String position;
    private String deviceType;
    private String userType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isAlwaysShow;
    private Integer clickCount;
    private Integer impressionCount;
    private Integer sortOrder;
    private Boolean isActive;
    private Boolean isDeleted;
    private String remark;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
