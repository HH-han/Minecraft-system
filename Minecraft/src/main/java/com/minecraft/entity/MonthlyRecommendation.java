package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("monthly_recommendations")
public class MonthlyRecommendation {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer monthId;
    
    private String destinationName;
    
    private String imageUrl;
    
    private String description;
    
    private String tags;
    
    private String seasonFeatures;
    
    private String climateInfo;
    
    private String travelTips;
    
    private Integer recommendedDays;
    
    private Integer sortOrder;
    
    private Integer isRecommended;
    
    private Integer viewCount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
