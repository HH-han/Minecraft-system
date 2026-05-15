package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("attraction_facility")
public class AttractionFacility {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long attractionId;
    
    private String facilityName;
    
    private String icon;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}