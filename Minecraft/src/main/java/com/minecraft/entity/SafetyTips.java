package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("safetytips")
public class SafetyTips {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private Long categoryId;
    private Integer sortWeight;
    private Integer status;
    private Long creatorId;
    private Long updaterId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
