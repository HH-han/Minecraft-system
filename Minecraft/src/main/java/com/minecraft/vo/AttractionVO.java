package com.minecraft.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AttractionVO {
    private Long id;
    private String name;
    private String city;
    private String province;
    private String address;
    private String description;
    private String coverImage;
    private List<String> images;
    private BigDecimal price;
    private String season;
    private Integer rating;
    private Integer likeCount;
    private Integer collectCount;
    private Integer commentCount;
    private List<String> tags;
    private Boolean isLiked;
    private Boolean isCollected;
    private LocalDateTime createTime;
}
