package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("points_products")
public class PointsProducts {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private Integer pointsPrice;
    private Integer stock;
    private String imageUrl;
    private Integer status; // 0:下架 1:上架
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}