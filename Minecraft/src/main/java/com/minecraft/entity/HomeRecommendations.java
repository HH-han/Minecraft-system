package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("home_recommendations")
public class HomeRecommendations {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String uuid;
    
    private String name;
    
    private String englishName;
    
    private String aliasName;
    
    private Integer categoryId;
    
    private String categoryName;
    
    private String tags;
    
    private String country;
    
    private String province;
    
    private String city;
    
    private String district;
    
    private String address;
    
    private BigDecimal longitude;
    
    private BigDecimal latitude;
    
    private String googleMapUrl;
    
    private String baiduMapUrl;
    
    private String transportation;
    
    private String recommendationReason;
    
    private String highlights;
    
    private String bestTravelTime;
    
    private String visitDuration;
    
    private String coverImageUrl;
    
    private String bannerImageUrl;
    
    private String videoUrl;
    
    private String imageUrls;
    
    private String ticketPriceDescription;
    
    private BigDecimal minPrice;
    
    private BigDecimal maxPrice;
    
    private String priceCurrency;
    
    private Boolean isFree;
    
    private String openingHoursWeekday;
    
    private String openingHoursWeekend;
    
    private String openingHoursDescription;
    
    private String contactPhone;
    
    private String officialWebsite;
    
    private String socialMedia;
    
    private Integer viewCount;
    
    private Integer likeCount;
    
    private Integer shareCount;
    
    private Integer commentCount;
    
    private BigDecimal averageRating;
    
    private Integer ratingCount;
    
    private Integer weight;
    
    private Boolean isHot;
    
    private Boolean isNew;
    
    private Boolean isTop;
    
    private Integer status;
    
    private String season;
    
    private String festival;
    
    private String tips;
    
    private String suitableCrowd;
    
    private String facilityInfo;
    
    private String seoTitle;
    
    private String seoKeywords;
    
    private String seoDescription;
    
    private String content;
    
    private String summary;
    
    private String source;
    
    private String author;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    private LocalDateTime publishedAt;
    
    private LocalDateTime expiredAt;
}