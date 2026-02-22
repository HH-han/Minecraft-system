package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("carousel_groups")
public class CarouselGroups {
    private Integer id;
    private String groupName;
    private String groupCode;
    private String description;
    private String displayType;
    private Boolean autoPlay;
    private Integer autoPlayInterval;
    private String animationType;
    private Boolean showIndicators;
    private Boolean showArrows;
    private Boolean pauseOnHover;
    private Integer width;
    private Integer height;
    private String aspectRatio;
    private Integer maxItems;
    private Boolean isActive;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
