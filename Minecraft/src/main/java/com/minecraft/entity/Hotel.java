package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName("hotel")
public class Hotel {
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
    
    private Integer starLevel;
    
    private Integer rating;
    
    private Integer likeCount;
    
    private Integer collectCount;
    
    private Integer commentCount;
    
    private String facilities;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField(exist = false)
    private List<Object> rooms;
    
    @TableField(exist = false)
    private List<String> facilityList;
}

