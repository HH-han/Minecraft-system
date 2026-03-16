package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("food")
public class Food {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String city;
    
    private String province;
    
    private String address;
    
    private String description;
    
    private String coverImage;
    
    private String images;
    
    private BigDecimal price;
    
    private String cuisineType;
    
    private Integer rating;
    
    private Integer likeCount;
    
    private Integer collectCount;
    
    private Integer commentCount;
    
    private String tags;

    private String commodity;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
