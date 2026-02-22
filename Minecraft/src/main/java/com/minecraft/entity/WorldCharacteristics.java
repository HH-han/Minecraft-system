package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("worldcharacteristics")
public class WorldCharacteristics {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String name;
    
    private String country;
    
    private String image;
    
    private String description;
    
    private String features;
    
    private String history;
    
    private String culture;
    
    private String tags;
    
    private String category;
    
    private String color;
}