package com.minecraft.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OfficialwebsiteDestinationsVO {
    private Integer id;
    private String name;
    private String category;
    private String description;
    private String imageUrl;
    private String duration;
    private BigDecimal price;
    private String priceCurrency;
    private Integer isFeatured;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}