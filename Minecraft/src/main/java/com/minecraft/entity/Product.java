package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String type;
    
    private String city;
    
    private String province;
    
    private String description;
    
    private String coverImage;
    
    private String images;
    
    private BigDecimal price;
    
    private Integer stock;
    
    private Integer rating;
    
    private Integer likeCount;
    
    private Integer collectCount;
    
    private Integer commentCount;
    
    private String tags;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
