package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("carousel_group_items")
public class CarouselGroupItems {
    private Integer id;
    private Integer groupId;
    private Integer carouselId;
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
