package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("attraction_images")
public class AttractionImages {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer attractionId;
    
    private String imageUrl;
    
    private String thumbnailUrl;
    
    private String title;
    
    private String description;
    
    private String photographer;
    
    private String license;
    
    private Boolean isPrimary;
    
    private Integer sortOrder;
    
    private Integer width;
    
    private Integer height;
    
    private Integer fileSize;
    
    private LocalDateTime uploadTime;
}
