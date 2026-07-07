package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("captcha_puzzle_config")
public class PuzzleConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("image_id")
    private String imageId;
    
    @TableField("piece_x")
    private Integer pieceX;
    
    @TableField("piece_y")
    private Integer pieceY;
    
    @TableField("piece_width")
    private Integer pieceWidth;
    
    @TableField("piece_height")
    private Integer pieceHeight;
    
    @TableField("target_x")
    private Integer targetX;
    
    @TableField("target_y")
    private Integer targetY;
    
    @TableField("slider_percent")
    private BigDecimal sliderPercent;
    
    private Integer version;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}