package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("carousel_stats")
public class CarouselStats {
    private Integer id;
    private Integer carouselId;
    private LocalDate statDate;
    private Integer clickCount;
    private Integer impressionCount;
    private Integer uniqueClickCount;
    private Integer uniqueImpressionCount;
    private Integer pcClickCount;
    private Integer mobileClickCount;
    private Integer pcImpressionCount;
    private Integer mobileImpressionCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
